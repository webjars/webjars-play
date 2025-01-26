package controllers

import jakarta.inject.{Inject, Singleton}

import org.webjars.play.{RequireJS, WebJarAssets, WebJarsUtil}
import play.api.mvc.{Action, InjectedController}

@Singleton
class Application @Inject() (indexTemplate: views.html.index) extends InjectedController {

  def index = Action {
    Ok(indexTemplate())
  }

}
