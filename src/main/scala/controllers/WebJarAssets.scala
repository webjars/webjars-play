package controllers

import org.webjars.services.{ RequirejsProducer }
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.Application
import play.Configuration
import play.api.Play.current
import scala.util.matching.Regex
import play.api.Play
import scala.collection.JavaConverters._
import org.apache.commons.lang.StringUtils
import java.util.Arrays
import scala.collection.immutable.TreeMap
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
 * loaded from. /webjars is the default.
 */
class WebJarAssets extends Controller with RequirejsProducer {

  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"
  val WebjarPathPrefixDefault = "/webjars"
  val WebjarPathPrefixProp = "org.webjars.play.webJarPathPrefix"

  val webJarFilterExpr = current.configuration.getString(WebjarFilterExprProp)
    .getOrElse(WebjarFilterExprDefault)
  val webJarPathPrefix = current.configuration.getString(WebjarPathPrefixProp)
    .getOrElse(WebjarPathPrefixDefault)

  val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, Play.application.classloader))

  /**
   * Returns the contents of a WebJar asset
   */
  def at(file: String): Action[AnyContent] = {
    Assets.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)
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
   * FIXME: Source the dependencies.
   */
  def requirejs = Action {
    Ok(produce(webJarAssetLocator.getFullPathIndex.asScala.mapValues { webJarPath =>
      val fullPath = webJarPathPrefix + webJarPath.stripPrefix(WebJarAssetLocator.WEBJARS_PATH_PREFIX)
      Route(fullPath, List[String]())
    }.toMap)).as(JAVASCRIPT)
  }
}

object WebJarAssets extends WebJarAssets
