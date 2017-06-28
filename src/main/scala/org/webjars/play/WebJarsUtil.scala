package org.webjars.play

import javax.inject.{Inject, Singleton}

import org.webjars.WebJarAssetLocator
import play.api.mvc.Call
import play.api.{Configuration, Environment}

import scala.util.matching.Regex

/**
  * Resolves WebJar paths
  *
  * <p>org.webjars.play.webJarFilterExpr can be used to declare a regex for the
  * files that should be looked for when searching within WebJars. By default
  * all files are searched for.
  */
@Singleton
class WebJarsUtil @Inject() (configuration: Configuration, environment: Environment) {

  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"

  private lazy val webJarFilterExpr = configuration.getOptional[String](WebjarFilterExprProp).getOrElse(WebjarFilterExprDefault)

  private val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, environment.classLoader))

  /**
    * Locate a file in a WebJar
    *
    * @example Passing in `jquery.min.js` will return `jquery/1.8.2/jquery.min.js` assuming the jquery WebJar version 1.8.2 is on the classpath
    *
    * @param file the file or partial path to find
    * @return the path to the file (sans-the webjars prefix)
    *
    */
  def locate(file: String): String = {
    webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

  /**
    * Locate a file in a WebJar
    *
    * @param webJar the WebJar artifactId
    * @param path the file or partial path to find
    * @return the path to the file (sans-the webjars prefix)
    *
    */
  def locate(webJar: String, path: String): String = {
    webJarAssetLocator.getFullPath(webJar, path).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
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
  def fullPath(webjar: String, path: String): String = {
    val version = webJarAssetLocator.getWebJars.get(webjar)
    s"$webjar/$version/$path"
  }


  /**
    * Locates a WebJar from a partial path and returns the reverse route
    *
    * @param path The partial path of a file in a WebJar
    * @return The reverse route to the WebJar asset
    */
  def url(path: String): Call = {
    routes.WebJarAssets.at(locate(path))
  }

  /**
    * Locates a WebJar from a partial path and returns the reverse route
    *
    * @param webJar The artifact name of the WebJar
    * @param path The partial path of a file in a WebJar
    * @return The reverse route to the WebJar asset
    */
  def url(webJar: String, path: String): Call = {
    routes.WebJarAssets.at(locate(webJar, path))
  }

  /**
    * Generates the RequireJS config and main script tags
    *
    * @param mainUrl The reverse route of the main app
    * @return The RequireJS config and main script tags
    */
  def requireJs(mainUrl: Call): String = {

    val setup = s"""<script src="${routes.RequireJS.setup().url}"></script>"""

    val requireRoute = url("requirejs", "require.min.js")

    val main = s"""<script data-main="${mainUrl.url}" src="${requireRoute.url}"></script>"""

    setup + main
  }

}
