package controllers

import javax.inject.{Singleton, Inject}

import org.webjars.play.RequireJS
import play.api.mvc.{Action, Controller}

@Singleton
class Application @Inject() (webJarAssets: WebJarAssets, requireJS: RequireJS) extends Controller {

  def index = Action {
    Ok(views.html.index(webJarAssets, requireJS))
  }

}