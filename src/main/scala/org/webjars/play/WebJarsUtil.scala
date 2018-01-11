package org.webjars.play

import javax.inject.{Inject, Singleton}

import org.webjars.WebJarAssetLocator
import play.api.mvc.Call
import play.api.{Configuration, Environment, Logger, Mode}
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
class WebJarsUtil @Inject() (configuration: Configuration, environment: Environment) {

  lazy val webJarFilterExpr: String = configuration.getOptional[String]("webjars.filter-expression").getOrElse(".*")
  lazy val cdnUrl: String = configuration.getOptional[String]("webjars.cdn-url").getOrElse("https://cdn.jsdelivr.net/webjars")
  lazy val useCdn: Boolean = configuration.getOptional[Boolean]("webjars.use-cdn").getOrElse(false)

  class WebJarAsset(val path: Try[String]) {
    /**
      * Get the groupId from a path
      *
      * @example groupId("jquery/1.9.0/jquery.js") will return "org.webjars" if the classpath contains the org.webjars jquery jar
      *
      * @param path a string to parse a WebJar name from
      * @return the artifact groupId based on the classpath
      */
    def groupId(path: String): Try[String] = {
      val webJar = path.split("/").head
      val suffix = s"/$webJar/pom.xml"
      val prefix = "META-INF/maven/"
      val fullPathTry = Try(webJarAssetLocator.getFullPath(suffix))

      fullPathTry.map(_.stripSuffix(suffix).stripPrefix(prefix))
    }

    private[this] def tag(f: String => Html): Html =
      url.fold(err => {
          val errMsg = s"couldn't find asset $path"
          Logger.error(errMsg, err)
          environment.mode match {
            case Mode.Prod =>
              Html("")
            case _ =>
              throw err
          }
      }, f)

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
      path.flatMap { p =>
        if (useCdn) {
          val groupIdTry = groupId(p)
          groupIdTry.map { groupId =>
            s"$cdnUrl/$groupId/$p"
          }
        }
        else {
          Success(routes.WebJarAssets.at(p).url)
        }
      }
  }

  private lazy val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, environment.classLoader
    )
  )

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
    new WebJarAsset(Try(
      removePrefix(webJarAssetLocator.getFullPath(file))))
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
    new WebJarAsset(Try(
      removePrefix(webJarAssetLocator.getFullPath(webJar, path))))
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
    new WebJarAsset(Try(removePrefix(webJarAssetLocator.getFullPathExact(webjar, path))))
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
