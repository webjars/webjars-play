package org.webjars.play

import java.io.File
import java.net.{URL, URLClassLoader}

import play.api.{Environment, Mode}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.{PlaySpecification, WithApplication}

class RequireJSSpec extends PlaySpecification {

  "The setup" should {
    "produce the requirejs setup" in new WithApplication {
      val requireJs = app.injector.instanceOf[RequireJS]

      val javaScript = requireJs.setup(Call("GET", "/assets/js/app"))

      javaScript must contain("return ['/webjars/' + webJarId")
      javaScript must contain("\"requirejs\":\"2.1.20\"")
      javaScript must contain("""<script data-main="/assets/js/app" src="/webjars/requirejs/2.1.20/require.min.js"></script>""")
    }
    "fail with a useful error when the required route is not found" in {

      val cl = new ClassLoader(getClass.getClassLoader) {
        override def loadClass(name: String, resolve: Boolean): Class[_] = {
          if (name == "controllers.routes") {
            throw new ClassNotFoundException()
          }
          else {
            super.loadClass(name, resolve)
          }
        }
      }

      val environment = Environment(new File("."), cl, Mode.Test)

      val app = GuiceApplicationBuilder(environment = environment).build()

      val requireJs = app.injector.instanceOf[RequireJS]

      requireJs.setup(Call("GET", "/assets/js/app")) must throwA [SetupException]
    }
  }

}
