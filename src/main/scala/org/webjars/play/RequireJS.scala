package org.webjars.play

import javax.inject.{Inject, Singleton}

import controllers.WebJarAssets
import play.api.Environment
import play.api.mvc.Call

import scala.language.reflectiveCalls

@Singleton
class RequireJS @Inject() (environment: Environment, webJarAssets: WebJarAssets) {

  def setup(mainUrl: Call): String = {

    // We have to use reflection because the reverse routing is not available in the library project
    def nastyReflectedRoute(path: String, routerName: String): Call = {
      val clazz = Class.forName("controllers.routes", true, environment.classLoader)
      val field = clazz.getDeclaredField(routerName)
      val routeInstance = field.get(null)
      routeInstance.asInstanceOf[{ def at(path: String): Call }].at(path)
    }

    def nastyReflectedWebJarAssetsRoute(path: String): Call = nastyReflectedRoute(path, "WebJarAssets")

    try {
      val setupJavaScript: String = org.webjars.RequireJS.getSetupJavaScript(nastyReflectedWebJarAssetsRoute("").url)

      val requireRoute = nastyReflectedWebJarAssetsRoute(webJarAssets.locate("require.min.js"))

      s"""<script>
        |    // this stuff must be done before require.js is loaded
        |    $setupJavaScript
        |</script>
        |<script data-main="${mainUrl.url}" src="$requireRoute"></script>""".stripMargin
    }
    catch {
      case e: Exception =>
        throw new SetupException("The RequireJS config could not be determined probably because you are missing a WebJarAssets route", e)
    }

  }

}

class SetupException(message: String, cause: Throwable) extends RuntimeException(message, cause)
