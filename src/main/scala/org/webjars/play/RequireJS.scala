package org.webjars.play

import play.api.mvc.Call

import scala.language.reflectiveCalls

object RequireJS {

  def setup(main: String): String = {

    // We have to use reflection because the reverse routing is not available in the library project
    def nastyReflectedRoute(path: String, routerName: String): Call = {
      val clazz = Class.forName("controllers.routes", true, play.api.Play.current.classloader)
      val field = clazz.getDeclaredField(routerName)
      val routeInstance = field.get(null)
      routeInstance.asInstanceOf[{ def at(path: String): Call }].at(path)
    }
    
    def nastyReflectedWebJarAssetsRoute(path: String): Call = nastyReflectedRoute(path, "WebJarAssets")
    def nastyReflectedAssetsRoute(path: String): Call = nastyReflectedRoute(path, "Assets")

    val setupJavaScript: String = org.webjars.RequireJS.getSetupJavaScript(nastyReflectedWebJarAssetsRoute("").url)
    
    val mainRoute = nastyReflectedAssetsRoute(main)

    val requireRoute = nastyReflectedWebJarAssetsRoute(controllers.WebJarAssets.locate("require.min.js"))
    
    s"""<script>
      |    // this stuff must be done before require.js is loaded
      |    $setupJavaScript
      |</script>
      |<script data-main="$mainRoute" src="$requireRoute"></script>""".stripMargin
  }
}
