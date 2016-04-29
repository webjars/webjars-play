package org.webjars.play

import controllers.WebJarAssets
import play.api.http.HttpErrorHandler
import play.api.{Configuration, Environment}

/**
 * Compile-time DI components
 */
trait WebJarComponents {

  def httpErrorHandler: HttpErrorHandler
  def configuration: Configuration
  def environment: Environment

  lazy val webJarAssets = new WebJarAssets(httpErrorHandler, configuration, environment)

}
