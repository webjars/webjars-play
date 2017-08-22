package org.webjars.play

import javax.inject.{Inject, Singleton}

import org.webjars.WebJarAssetLocator
import play.api.mvc.Call
import play.api.{Configuration, Environment, Logger, Mode}

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

  private lazy val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, environment.classLoader
    )
  )

  /**
    * Locate a file in a WebJar
    *
    * @example Passing in `jquery.min.js` will return `jquery/1.8.2/jquery.min.js` assuming the jquery WebJar version 1.8.2 is on the classpath
    *
    * @param file the file or partial path to find
    * @return the path to the file (sans-the webjars prefix)
    *
    */
  def locate(file: String): Try[String] = {
    Try(webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/"))
  }

  /**
    * Locate a file in a WebJar
    *
    * @param webJar the WebJar artifactId
    * @param path the file or partial path to find
    * @return the path to the file (sans-the webjars prefix)
    *
    */
  def locate(webJar: String, path: String): Try[String] = {
    Try(webJarAssetLocator.getFullPath(webJar, path).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/"))
  }

  /**
    * Get the full path to a file in a WebJar without validating that the file actually exists
    *
    * @example Calling fullPath("react", "react.js") will return the full path to the file in the WebJar because react.js exists at the root of the WebJar
    *
    * @param webjar the WebJar artifactId
    * @param path the full path to a file in the WebJar
    * @return the path to the file (sans-the webjars prefix)
    *
    */
  def fullPath(webjar: String, path: String): Try[String] = {
    val versionTry = Try(webJarAssetLocator.getWebJars.get(webjar))
    versionTry.map { version =>
      s"$webjar/$version/$path"
    }
  }

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

  /**
    * Based on config, either returns a local url or a cdn url for a given asset path
    *
    * @param pathTry Possibly a path to convert to a local url or cdn url
    * @return Possibly the local or cdn url
    */
  def localOrCdnUrl(pathTry: Try[String]): Try[String] = {
    pathTry.flatMap { path =>
      if (useCdn) {
        val groupIdTry = groupId(path)
        groupIdTry.map { groupId =>
          s"$cdnUrl/$groupId/$path"
        }
      }
      else {
        Success(routes.WebJarAssets.at(path).url)
      }
    }
  }


  /**
    * Locates a WebJar from a partial path and returns the reverse route or CDN URL
    *
    * @param path The partial path of a file in a WebJar
    * @return The reverse route to the WebJar asset
    */
  def url(path: String): Try[String] = {
    localOrCdnUrl(locate(path))
  }

  /**
    * Locates a WebJar from a partial path and returns the reverse route
    *
    * @param webJar The artifact name of the WebJar
    * @param path The partial path of a file in a WebJar
    * @return The reverse route to the WebJar asset
    */
  def url(webJar: String, path: String): Try[String] = {
    localOrCdnUrl(locate(webJar, path))
  }

  /**
    * Turns a possible url into a tag using a provided function.  Uses the mode of the application to determine if an error should be returned or not.
    *
    * @param urlTry A possible url
    * @param f A function that will render a successful url
    * @return The tag to be rendered or when there is an error, an emtpy string in Prod mode and an error comment otherwise
    */
  def tag(urlTry: Try[String])(f: String => String): String = {
    urlTry match {
      case Success(url) =>
        f(url)
      case Failure(e) =>
        Logger.error("Could not get URL", e)
        environment.mode match {
          case Mode.Prod =>
            ""
          case _ =>
            s"""<!-- Could not get URL: ${e.getMessage} -->"""
        }
    }
  }

  /**
    * Turns a path into a tag using a provided function.  Uses the mode of the application to determine if an error should be returned or not.
    *
    * @param path The partial path of a file in a WebJar
    * @param f A function that will render a successful url
    * @return The tag to be rendered or when there is an error, an emtpy string in Prod mode and an error comment otherwise
    *
    */
  def tag(path: String)(f: String => String): String = {
    tag(url(path))(f)
  }

  /**
    * A script tag
    *
    * @param urlTry A possible url
    * @param otherParams Other params for the script tag
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def script(urlTry: Try[String], otherParams: Map[String, String] = Map.empty[String, String]): String = {
    tag(urlTry) { url =>
      val params = otherParams.map { case (name, value) =>
        s"""$name="$value""""
      }.mkString(" ")

      s"""<script src="$url" $params></script>"""
    }
  }

  /**
    * A script tag
    *
    * @param call A call that becomes a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def script(call: Call): String = {
    script(Success(call.url))
  }

  /**
    * A script tag
    *
    * @param path A path that becomes a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def script(path: String): String = {
    script(url(path))
  }

  /**
    * A script tag
    *
    * @param webJar Name of the WebJar
    * @param path A path to an asset in the WebJar
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def script(webJar: String, path: String): String = {
    script(url(webJar, path))
  }

  /**
    * A script tag
    *
    * @param webJar Name of the WebJar
    * @param path A path to an asset in the WebJar
    * @param otherParams Other params to add to the script tag
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def scriptWithParams(webJar: String, path: String, otherParams: Map[String, String]): String = {
    script(url(webJar, path), otherParams)
  }

  /**
    * A CSS link tag
    *
    * @param urlTry A possible url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def css(urlTry: Try[String]): String = {
    tag(urlTry) { url =>
      s"""<link rel="stylesheet" type="text/css" href="$url">"""
    }
  }

  /**
    * A CSS link tag
    *
    * @param call A call that will become a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def css(call: Call): String = {
    css(Success(call.url))
  }

  /**
    * A CSS link tag
    *
    * @param path A call that will become a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def css(path: String): String = {
    css(url(path))
  }

  /**
    * A CSS link tag
    *
    * @param webJar Name of the WebJar
    * @param path A path to an asset in the WebJar
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def css(webJar: String, path: String): String = {
    css(url(webJar, path))
  }

  /**
    * A img tag
    *
    * @param urlTry A possible url
    * @param otherParams Other params for the script tag
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def img(urlTry: Try[String], otherParams: Map[String, String] = Map.empty[String, String]): String = {
    tag(urlTry) { url =>
      val params = otherParams.map { case (name, value) =>
        s"""$name="$value""""
      }.mkString(" ")

      s"""<img src="$url" $params>"""
    }
  }

  /**
    * A img tag
    *
    * @param call A call that becomes a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def img(call: Call): String = {
    img(Success(call.url))
  }

  /**
    * A img tag
    *
    * @param path A path that becomes a url
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def img(path: String): String = {
    img(url(path))
  }

  /**
    * A script tag
    *
    * @param webJar Name of the WebJar
    * @param path A path to an asset in the WebJar
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def img(webJar: String, path: String): String = {
    img(url(webJar, path))
  }

  /**
    * A img tag
    *
    * @param path A path to an asset in the WebJar
    * @param otherParams Other params to add to the script tag
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def imgWithParams(path: String, otherParams: Map[String, String]): String = {
    img(url(path), otherParams)
  }

  /**
    * A img tag
    *
    * @param webJar Name of the WebJar
    * @param path A path to an asset in the WebJar
    * @param otherParams Other params to add to the script tag
    * @return A string with the tag or an error / empty string (depending on mode)
    */
  def imgWithParams(webJar: String, path: String, otherParams: Map[String, String]): String = {
    img(url(webJar, path), otherParams)
  }

  /**
    * Generates the RequireJS config and main script tags
    *
    * @param mainUrl The reverse route of the main app
    * @return The RequireJS config and main script tags
    */
  def requireJs(mainUrl: Call): String = {
    val setup = script(routes.RequireJS.setup())

    val main = scriptWithParams("requirejs", "require.min.js", Map("data-main" -> mainUrl.url))

    setup + main
  }

}
