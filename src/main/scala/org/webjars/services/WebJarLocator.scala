package org.webjars.services

import java.io.File
import scala.collection.JavaConversions.enumerationAsScalaIterator
import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.asScalaSet
import org.reflections.Reflections
import org.reflections.util.{ ClasspathHelper, ConfigurationBuilder }
import org.reflections.scanners.ResourcesScanner
import org.reflections.Store
import scala.collection.JavaConversions._
import java.util.Collection
import com.sun.xml.internal.bind.v2.schemagen.MultiMap
import scala.util.matching.Regex

/**
 * The Locator is responsible for locating webjar resources. The trait has
 * implicit knowledge in terms of where webjars are located on the classpath.
 */
trait WebJarLocator {

  val RESOURCES_SCANNER = "ResourcesScanner"
  val WEBJARS_PACKAGE = "META-INF.resources.webjars"
  val WEBJARS_PATH_PREFIX = "META-INF/resources/webjars"

  /**
   * Locate a specific resource given its filename and return a relative path
   */
  def locate(routes: Map[String, String], file: String): String = {

    // the map in the reflection store is just the file name so if the file being located doesn't contain a "/" then
    // a shortcut can be taken.  Otherwise the collection of multimap's values need to be searched.
    // Either way the first match is returned (if there is a match)
    if (file.contains("/")) {
      routes.values.find(_.endsWith(file)).head
    } else {
      routes.get(file).get
    }
  }

  /**
   * Return a mapping of webjar files and relative path locations (routes)
   */
  def locateAll(filterExpr: Regex, classLoaders: ClassLoader*): Map[String, String] = {
    val config = new ConfigurationBuilder()
      .addUrls(ClasspathHelper.forPackage(WEBJARS_PACKAGE, classLoaders: _*))
      .setScanners(new ResourcesScanner())

    val reflections = new Reflections(config)

    reflections.getStore().getStoreMap().get(RESOURCES_SCANNER).asMap()
      .filterKeys(file => filterExpr.findFirstIn(file).isDefined)
      .mapValues(value => relpath(value.head))
      .toMap
  }

  // Strip the leading path in order to produce a relative path for web consumption
  private def relpath(file: String): String = {
    file.stripPrefix(WEBJARS_PATH_PREFIX + "/")
  }
}
