package controllers

import play.api.mvc.{Action, AnyContent}
import play.api.Play
import play.api.Play.current

import java.io.File
import scala.collection.JavaConversions.enumerationAsScalaIterator
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.asScalaSet
import org.reflections.Reflections
import org.reflections.util.{ClasspathHelper, ConfigurationBuilder}
import org.reflections.scanners.ResourcesScanner

object WebJarAssets {
  
  val WEBJARS_PATH_PREFIX = List("META-INF", "resources", "webjars")

  // returns the contents of a webjar asset
  def at(file: String): Action[AnyContent] = {
    Assets.at("/" + WEBJARS_PATH_PREFIX.mkString("/"), file)
  }
  
  // this resolves a full path to a webjar asset based on a file suffix
  // todo: cache this
  def locate(file: String): String = {
    val config = new ConfigurationBuilder()
                        .addUrls(ClasspathHelper.forPackage(WEBJARS_PATH_PREFIX.mkString("."), Play.application.classloader))
                        .setScanners(new ResourcesScanner())
    
    val reflections = new Reflections(config)
    
    println(reflections.getStore.getStoreMap.values)
    
    // the map in the reflection store is just the file name so if the file being located doesn't contain a "/" then
    // a shortcut can be taken.  Otherwise the collection of multimap's values need to be searched.
    // Either way the first match is returned (if there is a match)
    if (file.contains("/")) {
      reflections.getStore.getStoreMap.values.map(_.values.find(_.endsWith(file))).head.get.stripPrefix(WEBJARS_PATH_PREFIX.mkString("/") + "/")
    }
    else {
      reflections.getStore.getResources(file).head.stripPrefix(WEBJARS_PATH_PREFIX.mkString("/") + "/")
    }
  }

}

