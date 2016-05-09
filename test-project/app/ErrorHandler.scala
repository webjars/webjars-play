import javax.inject._

import controllers.WebJarAssets

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._

class ErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    webJarAssets: WebJarAssets
  ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful {
      NotFound(views.html.error(webJarAssets, "Not Found: " + request.uri))
    }
  }

}
