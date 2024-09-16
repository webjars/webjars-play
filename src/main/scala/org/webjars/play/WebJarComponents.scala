package org.webjars.play

import controllers.AssetsMetadata
import play.api.http.HttpErrorHandler
import play.api.{Configuration, Environment}

/**
 * Compile-time DI components
 */
trait WebJarComponents {

  def configuration: Configuration
  def environment: Environment
  def httpErrorHandler: HttpErrorHandler
  def assetsMetadata: AssetsMetadata

  lazy val webJarsUtil = new WebJarsUtil(configuration, environment)

  lazy val webJarAssets = new WebJarAssets(httpErrorHandler, assetsMetadata, environment)

}
