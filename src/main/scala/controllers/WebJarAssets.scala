package controllers

import org.webjars.services.{ RequirejsProducer }
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Play.current
import scala.util.matching.Regex
import play.api.Play
import scala.collection.JavaConverters._
import org.webjars.WebJarAssetLocator
import org.webjars.services.Route

/**
 * A Play framework controller that is able to resolve webjar paths.
 * In addition a various JavaScript modules can be produced that are able to
 * resolve WebJar paths on the client side. requirejs is an example of this.
 * <p>org.webjars.play.webJarFilterExpr can be used to declare a regex for the
 * files that should be looked for when searching within webjars. By default
 * all files are searched for.
 * <p>org.webjars.play.webJarPathPrefix specifies where WebJar resources are
 * loaded from. 'webjars' is the default.
 */
class WebJarAssets(assetsBuilder: AssetsBuilder) extends Controller with RequirejsProducer {

  val AppContextDefault = "/"
  val AppContextProp = "application.context"
  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"
  val WebjarPathPrefixDefault = "webjars"
  val WebjarPathPrefixProp = "org.webjars.play.webJarPathPrefix"

  val WebjarRequireJsConfigFile = "/webjars-requirejs.js"

  val webJarFilterExpr = current.configuration.getString(WebjarFilterExprProp)
    .getOrElse(WebjarFilterExprDefault)
  val webJarPathPrefix =
    current.configuration.getString(AppContextProp).getOrElse(AppContextDefault) +
    current.configuration.getString(WebjarPathPrefixProp).getOrElse(WebjarPathPrefixDefault)

  val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, Play.application.classloader))

  val routes = webJarAssetLocator.getFullPathIndex.asScala.filter { pair =>
    pair._2.lengthCompare(WebJarAssetLocator.WEBJARS_PATH_PREFIX.length) > 0 &&
      pair._2.startsWith(WebJarAssetLocator.WEBJARS_PATH_PREFIX)
  }.mapValues { webJarPath =>
    val relWebJarPath = webJarPath.stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX)
    val assetPath = webJarPathPrefix + relWebJarPath
    val requireJsConfigPath = webJarPathPrefix + requirejsConfigPath(relWebJarPath)
    Route(assetPath, List[String](requireJsConfigPath))
  }.toMap

  /**
   * Returns the contents of a WebJar asset
   */
  def at(file: String, locate: Boolean = false): Action[AnyContent] = {
    val locatedFile = if (locate) this.locate(file) else file
    assetsBuilder.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, locatedFile)
  }

  /**
   * Return the path of a WebJar asset
   * Transforms a file lookup like "jquery.min.js" to the path sans-the webjars prefix,
   * like "jquery/1.8.2/jquery.min.js"
   *
   */
  def locate(file: String): String = {
    webJarAssetLocator.getFullPath(file).stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX + "/")
  }

  /**
   * Return the bootstrapping required for require.js so that assets can be
   * located using the "webjars!" loader plugin convention.
   */
  def requirejs = Action {
    Ok(produce(routes)).as(JAVASCRIPT)
  }

  /*
   * Extract a requirejs configuration path given a relative webjar path.
   */
  private def requirejsConfigPath(relWebJarPath: String): String = {
    val artifactIdDelim = relWebJarPath.indexOf('/', 1)
    if (artifactIdDelim < 0) {
      return ""
    }
    val artifactId = relWebJarPath.substring(0, artifactIdDelim)
    val versionDelim = relWebJarPath.indexOf('/', artifactIdDelim + 1)
    if (versionDelim < 0) {
      return ""
    }
    val version = relWebJarPath.substring(artifactIdDelim, versionDelim)
    artifactId + version + WebjarRequireJsConfigFile
  }
}

object WebJarAssets extends WebJarAssets(Assets)
