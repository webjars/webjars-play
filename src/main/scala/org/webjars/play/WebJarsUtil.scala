package org.webjars.play

import jakarta.inject.{Inject, Singleton}
import org.webjars.WebJarAssetLocator
import play.api.mvc.Call
import play.api.{Configuration, Environment, Logging, Mode}
import play.twirl.api.{Html, HtmlFormat}

import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex

/**
  * WebJars Util
  *
  * Config:
  *
  * webjars.filter-expression can be used to declare a regex for the
  * files that should be looked for when searching within WebJars. By default
  * all files are searched for.
  *
  * webjars.cdn-url overrides the default CDN url (https://cdn.jsdelivr.net/webjars)
  *
  * webjars.use-cdn toggles the CDN
  */
@Singleton
class WebJarsUtil @Inject() (configuration: Configuration, environment: Environment) extends Logging {

  lazy val webJarFilterExpr: String = configuration.getOptional[String]("webjars.filter-expression").getOrElse(".*")
  lazy val cdnUrl: String = configuration.getOptional[String]("webjars.cdn-url").getOrElse("https://cdn.jsdelivr.net/webjars")
  lazy val useCdn: Boolean = configuration.getOptional[Boolean]("webjars.use-cdn").getOrElse(false)

  class WebJarAsset(val fullPath: Try[String]) {
    lazy val path: Try[String] = fullPath.map(removePrefix)

    private[this] def tag(f: String => Html): Html = {
      url match {
        case Success(assetUrl) =>
          f(assetUrl)
        case Failure(err) =>
          val errMsg = s"couldn't find asset $path"
          logger.error(errMsg, err)
          environment.mode match {
            case Mode.Prod =>
              Html("")
            case _ =>
              throw err
          }
      }
    }

    def script(params: Map[String, String] = Map.empty): Html =
      tag(html.script(_, params))

    /**
      * A CSS link tag
      *
      * @return An Html with the tag or an error / empty string (depending on mode)
      */
    def css(params: Map[String, String] = Map.empty): Html =
      tag(html.css(_, params))

    /**
      * A img tag
      *
      * @return An Html with the tag or an error / empty string (depending on mode)
      */

    def img(params: Map[String, String] = Map.empty): Html =
      tag(html.img(_, params))

    /**
      * Get the asset's reverse route
      */
    def url: Try[String] =
      fullPath.flatMap { fullPath =>
        path.flatMap { path =>
          if (useCdn) {
            val groupIdTry = Try(Option(webJarAssetLocator.groupId(fullPath)).get)
            groupIdTry.map { groupId =>
              s"$cdnUrl/$groupId/$path"
            }
          }
          else {
            Success(routes.WebJarAssets.at(path).url)
          }
        }
      }
  }

  private lazy val webJarAssetLocator = new WebJarAssetLocator()

  private[this] def removePrefix(s: String): String =
    s.stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")

  /**
    * Locate a file in a WebJar
    *
    * @example Passing in `jquery.min.js` will return a WebJarAsset,
    *          assuming the jquery WebJar version 1.8.2 is on the classpath
    *
    * @param file the file or partial path to find
    * @return the located WebJarAsset
    *
    */
  def locate(file: String): WebJarAsset = {
    new WebJarAsset(Try(webJarAssetLocator.getFullPath(file)))
  }

  /**
    * Locate a file in a WebJar
    *
    * @param webJar the WebJar artifactId
    * @param path the file or partial path to find
    * @return the located WebJarAsset
    *
    */
  def locate(webJar: String, path: String): WebJarAsset = {
    new WebJarAsset(Try(webJarAssetLocator.getFullPath(webJar, path)))
  }

  /**
    * Get the full path to a file in a WebJar without validating that the file actually exists
    *
    * @example Calling fullPath("react", "react.js") will return
    *          a WebJarAsset because react.js exists at the root of the WebJar
    *
    * @param webjar the WebJar artifactId
    * @param path the full path to a file in the WebJar
    * @return the located WebJarAsset
    *
    */
  def fullPath(webjar: String, path: String): WebJarAsset = {
    new WebJarAsset(Try(webJarAssetLocator.getFullPathExact(webjar, path)))
  }

  /**
    * Generates the RequireJS config and main script tags
    *
    * @param mainUrl The reverse route of the main app
    * @return The RequireJS config and main script tags
    */
  def requireJs(mainUrl: Call): Html = {
    val setup: Html = html.script(routes.RequireJS.setup().url)

    val main: Html = fullPath("requirejs", "require.min.js").script(Map("data-main" -> mainUrl.url))

    HtmlFormat.fill(List(setup, main))
  }

}
