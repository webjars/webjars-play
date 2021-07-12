package org.webjars.play

import javax.inject.{Inject, Singleton}

import play.api.http.MimeTypes
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

@Singleton
class RequireJS @Inject() (webJarsUtil: WebJarsUtil, controllerComponents: ControllerComponents)
  extends AbstractController(controllerComponents) {

  def setup(): Action[AnyContent] = Action {
    val setupJavaScript = if (webJarsUtil.useCdn) {
      org.webjars.RequireJS.getSetupJavaScript(webJarsUtil.cdnUrl + "/", routes.WebJarAssets.at("").url)
    }
    else {
      org.webjars.RequireJS.getSetupJavaScript(routes.WebJarAssets.at("").url)
    }
    Ok(setupJavaScript).as(MimeTypes.JAVASCRIPT)
  }

}
