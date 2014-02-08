package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.Play.current
import scala.util.matching.Regex
import play.api.Play
import scala.collection.JavaConverters._
import org.webjars.WebJarAssetLocator

/**
 * A Play framework controller that is able to resolve webjar paths.
 * <p>org.webjars.play.webJarFilterExpr can be used to declare a regex for the
 * files that should be looked for when searching within webjars. By default
 * all files are searched for.
 */
class WebJarAssets(assetsBuilder: AssetsBuilder) extends Controller {

  val WebjarFilterExprDefault = """.*"""
  val WebjarFilterExprProp = "org.webjars.play.webJarFilterExpr"

  val webJarFilterExpr = current.configuration.getString(WebjarFilterExprProp).getOrElse(WebjarFilterExprDefault)

  val webJarAssetLocator = new WebJarAssetLocator(
    WebJarAssetLocator.getFullPathIndex(
      new Regex(webJarFilterExpr).pattern, Play.application.classloader))

  /**
   * Returns the contents of a WebJar asset
   */
  def at(file: String): Action[AnyContent] = {
    assetsBuilder.at("/" + WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)
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

}

object WebJarAssets extends WebJarAssets(Assets)
