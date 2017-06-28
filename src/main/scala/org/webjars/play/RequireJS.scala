package org.webjars.play

import javax.inject.{Inject, Singleton}

import play.api.http.MimeTypes
import play.api.mvc.{Action, AnyContent, InjectedController}

@Singleton
class RequireJS extends InjectedController {

  def setup(): Action[AnyContent] = Action {
    Ok(org.webjars.RequireJS.getSetupJavaScript(routes.WebJarAssets.at("").url)).as(MimeTypes.JAVASCRIPT)
  }

}
