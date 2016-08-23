package org.webjars.play

import controllers.WebJarAssets
import play.api.{Configuration, Environment}
import play.api.inject.Module

class WebJarModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[WebJarAssets].toSelf
  )

}
