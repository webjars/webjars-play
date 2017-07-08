package org.webjars.play

import javax.inject.{Inject, Singleton}

import play.api.http.MimeTypes
import play.api.mvc.{Action, AnyContent, InjectedController}

@Singleton
class RequireJS @Inject() (webJarsUtil: WebJarsUtil) extends InjectedController {

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
