package org.webjars.play


import controllers.AssetsMetadata
import org.webjars.WebJarVersionLocator
import play.api.Environment
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent}

import javax.inject.Inject

// this is needed to avoid conflicts with the controllers.Assets controller
class WebJarAssets @Inject() (errorHandler: HttpErrorHandler, assetsMetadata: AssetsMetadata, env: Environment) extends controllers.AssetsBuilder(errorHandler, assetsMetadata, env) {

  override def at(file: String): Action[AnyContent] = {
    at("/" + WebJarVersionLocator.WEBJARS_PATH_PREFIX, file)
  }

}

