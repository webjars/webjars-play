package org.webjars.play

import jakarta.inject.{Inject, Singleton}
import org.webjars.WebJarVersionLocator
import play.api.mvc.Call
import play.api.{Configuration, Environment, Logging, Mode}
import play.twirl.api.{Html, HtmlFormat}

import scala.util.{Failure, Success, Try}

/**
  * WebJars Util
  *
  * webjars.cdn-url overrides the default CDN url (https://cdn.jsdelivr.net/webjars)
  *
  * webjars.use-cdn toggles the CDN
  */
@Singleton
class WebJarsUtil @Inject() (configuration: Configuration, environment: Environment) extends Logging {
  lazy val cdnUrl: String = configuration.getOptional[String]("webjars.cdn-url").getOrElse("https://cdn.jsdelivr.net/webjars")
  lazy val useCdn: Boolean = configuration.getOptional[Boolean]("webjars.use-cdn").getOrElse(false)

  class WebJarAsset(val webjar: String, val fullPath: Try[String]) {
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
      path.flatMap { path =>
        if (useCdn) {
          val groupIdTry = Try(Option(webJarAssetLocator.groupId(webjar)).get)
          groupIdTry.map { groupId =>
            s"$cdnUrl/$groupId/$path"
          }
        }
        else {
          Success(routes.WebJarAssets.at(path).url)
        }
      }
  }

  private lazy val webJarAssetLocator = new WebJarVersionLocator()

  private[this] def removePrefix(s: String): String =
    s.stripPrefix(WebJarVersionLocator.WEBJARS_PATH_PREFIX + "/")


  def locate(webjar: String, path: String): WebJarAsset = {
    new WebJarAsset(webjar, Try(Option(webJarAssetLocator.fullPath(webjar, path)).get))
  }

  /**
    * Generates the RequireJS config and main script tags
    *
    * @param mainUrl The reverse route of the main app
    * @return The RequireJS config and main script tags
    */
  def requireJs(mainUrl: Call): Html = {
    val setup: Html = html.script(routes.RequireJS.setup().url)

    val main: Html = locate("requirejs", "require.min.js").script(Map("data-main" -> mainUrl.url))

    HtmlFormat.fill(List(setup, main))
  }

}
