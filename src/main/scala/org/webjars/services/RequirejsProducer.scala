package org.webjars.services

import play.api.libs.json._
import play.api.Play._
import play.api.Play

case class Route(fullPath: String, dependencies: List[String])

object RouteSerializer {
  implicit def routeWriter: Writes[Route] = Json.writes[Route]
}

/**
 * Builds require.js bootstrap code whereby all of the webjar resources are
 * resolved and declared to a require.js loader. The real require.js is then
 * loaded taking into consideration this configuration.
 *
 */
trait RequirejsProducer {

  import io.Source._
  import RouteSerializer._

  val RequirejsBootstrapScript = "requirejsBootstrap.js"
  val RoutesParameter = """["routes"]"""

  val requirejsBootstrapScript = fromInputStream(
    Play.current.classloader.getResourceAsStream(RequirejsBootstrapScript)).mkString

  def produce(routes: Map[String, Route]): String = {
    requirejsBootstrapScript
      .replace(RoutesParameter, Json.stringify(Json.toJson(routes)))
  }
}

