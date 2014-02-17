package org.webjars.play

import play.api.mvc.Call

object RequireJS {

  def setup(main: String): String = {

    // We have to use reflection because the reverse routing is not available in the library project
    def nastyReflectedRoute(path: String, routerName: String): Call = {
      // We have to use the current Play app's classloader otherwise we won't be able to find the reverse routers
      val c = Class.forName("controllers.Reverse" + routerName, true, play.api.Play.current.classloader)
      val m = c.getMethod("at", classOf[String])
      m.invoke(c.newInstance(), path).asInstanceOf[Call]
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
