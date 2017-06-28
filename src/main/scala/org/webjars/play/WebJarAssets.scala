package org.webjars.play


import javax.inject.Inject

import controllers.AssetsMetadata
import play.api.http.HttpErrorHandler
import play.api.mvc.{Action, AnyContent}

// this is needed to avoid conflicts with the controllers.Assets controller
class WebJarAssets @Inject() (errorHandler: HttpErrorHandler, assetsMetadata: AssetsMetadata) extends controllers.AssetsBuilder(errorHandler, assetsMetadata) {

  override def at(file: String): Action[AnyContent] = {
    this.at("/" + org.webjars.WebJarAssetLocator.WEBJARS_PATH_PREFIX, file)
  }

}

