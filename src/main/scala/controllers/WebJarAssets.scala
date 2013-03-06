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
import org.webjars.AssetLocator
import scala.collection.JavaConverters._
import org.apache.commons.lang.StringUtils
import java.util.Arrays

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

  val WEBJAR_FILTER_EXPR_DEFAULT = """.*"""
  val WEBJAR_FILTER_EXPR_PROP = "org.webjars.play.webJarFilterExpr"
  val WEBJAR_PATH_PREFIX_DEFAULT = "/webjars"
  val WEBJAR_PATH_PREFIX_PROP = "org.webjars.play.webJarPathPrefix"

  val webJarFilterExpr = current.configuration.getString(WEBJAR_FILTER_EXPR_PROP)
    .getOrElse(WEBJAR_FILTER_EXPR_DEFAULT)
  val webJarPathPrefix = new Regex(current.configuration.getString(WEBJAR_PATH_PREFIX_PROP)
    .getOrElse(WEBJAR_PATH_PREFIX_DEFAULT))

  def routes = AssetLocator.listAssets("/").asScala
    .filter { path =>
      webJarPathPrefix.findFirstIn(path).isDefined
    }
    .map { path =>
      (path.split("/").last, path)
    }
    .toMap[String, String]

  /**
   * Returns the contents of a WebJar asset
   */
  def at(file: String): Action[AnyContent] = {
    Assets.at("/" + Arrays.asList(AssetLocator.WEBJARS_PATH_PREFIX).asScala.mkString("/"), file) // path = /META-INF/resources/webjars
  }

  /**
   * Return the path of a WebJar asset
   * Transforms a file lookup like "jquery.min.js" to the path sans-the webjars prefix,
   * like "jquery/1.8.2/jquery.min.js"
   *
   */
  def locate(file: String): String = {
    AssetLocator.getWebJarPath(file)
  }

  /**
   * Return the bootstrapping required for require.js so that assets can be
   * located using the "webjars!" loader plugin convention.
   */
  def requirejs = Action {
    // todo: give this a list of files
    Ok(produce(routes.mapValues(webJarPath => webJarPathPrefix + "/" + webJarPath))).as(JAVASCRIPT)
  }
}

object WebJarAssets extends WebJarAssets
