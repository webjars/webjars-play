package org.webjars.play

import controllers.{AssetsMetadata, WebJarAssets}
import play.api.http.HttpErrorHandler
import play.api.{Configuration, Environment}

/**
 * Compile-time DI components
 */
trait WebJarComponents {

  def httpErrorHandler: HttpErrorHandler
  def configuration: Configuration
  def environment: Environment
  def meta: AssetsMetadata

  lazy val webJarAssets = new WebJarAssets(httpErrorHandler, meta, configuration, environment)

}
