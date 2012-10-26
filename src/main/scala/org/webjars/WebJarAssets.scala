package org.webjars

import play.api.mvc.{Call, Action, AnyContent}
import play.api.Play
import play.api.Play.current
import controllers.Assets

import scala.collection.JavaConversions.enumerationAsScalaIterator
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.asScalaSet
import org.reflections.Reflections
import org.reflections.util.{ClasspathHelper, ConfigurationBuilder}
import org.reflections.scanners.ResourcesScanner

object WebJarAssets {
  
  val WEBJARS_PATH_PREFIX = "META-INF/resources/webjars"
  
  // provides the reverse route to a webjar asset using just the suffix of the path to the asset
  def find(file: String): Call = {
    locate(file) match {
      case Some(path) => Call("GET", "/webjars/" + path.stripPrefix(WEBJARS_PATH_PREFIX + "/")) //routes.WebJarAssets.at(path.stripPrefix(WEBJARS_PATH_PREFIX + "/"))
      case None => Call("GET", "404") // todo: come up with a better way to 404
    }
  }

  // returns the contents of a webjar asset
  def at(file: String): Action[AnyContent] = {
    Assets.at("/" + WEBJARS_PATH_PREFIX, file)
  }
  
  // this resolves a full path to a webjar asset based on a file suffix
  // todo: cache this
  def locate(file: String): Option[String] = {
    val config = new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage("META-INF.resources.webjars", Play.application.classloader))
                        .setScanners(new ResourcesScanner())
    
    val reflections = new Reflections(config)
    
    // the map in the reflection store is just the file name so if the file being located doesn't contain a "/" then
    // a shortcut can be taken.  Otherwise the collection of multimap's values need to be searched.
    // Either way the first match is returned (if there is a match)
    file.contains("/") match {
      case false => reflections.getStore.getResources(file).headOption
      case true => reflections.getStore.getStoreMap.values.map(_.values.find(_.endsWith(file))).head
    }
  }

}

