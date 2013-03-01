package controllers

import play.api.mvc.{ Action, AnyContent }
import play.api.Play
import play.api.Play.current
import java.io.File
import scala.collection.JavaConversions.enumerationAsScalaIterator
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.asScalaSet
import org.reflections.Reflections
import org.reflections.util.{ ClasspathHelper, ConfigurationBuilder }
import org.reflections.scanners.ResourcesScanner
import play.api.mvc.Controller
import org.reflections.Store
import scala.collection.JavaConversions._

object WebJarAssets extends Controller {

  val WEBJARS_PATH_PREFIX = List("META-INF", "resources", "webjars")

  // returns the contents of a webjar asset
  def at(file: String): Action[AnyContent] = {
    Assets.at("/" + WEBJARS_PATH_PREFIX.mkString("/"), file)
  }

  private def formatJsonRoutes(): String = {
    val sb = new StringBuilder
    sb += '{'
    var first = true
    for (entry <- getStore.getStoreMap.get("ResourcesScanner").entries()) {
      if (first) {
        first = false
      } else {
        sb ++= ", "
      }
      val strippedPath = entry.getValue().stripPrefix(WEBJARS_PATH_PREFIX.mkString("/") + "/")
      sb ++= s""" "${entry.getKey()}": "/webjars/${strippedPath}" """
    }
    sb += '}'
    sb.toString
  }

  private def getStore(): Store = {
    val config = new ConfigurationBuilder()
      .addUrls(ClasspathHelper.forPackage(WEBJARS_PATH_PREFIX.mkString("."), Play.application.classloader))
      .setScanners(new ResourcesScanner())

    val reflections = new Reflections(config)

    reflections.getStore()
  }

  // this resolves a full path to a webjar asset based on a file suffix
  // todo: cache this
  def locate(file: String): String = {

    // the map in the reflection store is just the file name so if the file being located doesn't contain a "/" then
    // a shortcut can be taken.  Otherwise the collection of multimap's values need to be searched.
    // Either way the first match is returned (if there is a match)
    if (file.contains("/")) {
      getStore.getStoreMap.values.map(_.values.find(_.endsWith(file))).head.get.stripPrefix(WEBJARS_PATH_PREFIX.mkString("/") + "/")
    } else {
      getStore.getResources(file).head.stripPrefix(WEBJARS_PATH_PREFIX.mkString("/") + "/")
    }
  }

  // Build a require.js bootstrap whereby all of the webjar resources are 
  // resolved and declared to a require.js loader. The real require.js is then 
  // loaded taking into consideration this configuration.
  def requirejs = Action {
    val script = s"""
    var require;
    (function() {
      var routes = ${formatJsonRoutes};
      var webjarLoader = function(name, req, onload, config) {
        req([routes[name]], function(value) {
          onload(value);
        });
      }
      require = {
        callback: function() {
          define("webjars", function() {
            return {load: webjarLoader};
          });
        }
      };
      var script = document.createElement("script");
      script.setAttribute("type", "application/javascript");
      script.setAttribute("src", routes["require.js"]);
      document.getElementsByTagName("head")[0].appendChild(script);
    }());
            """

    Ok(script).as("application/javascript");
  }
}

