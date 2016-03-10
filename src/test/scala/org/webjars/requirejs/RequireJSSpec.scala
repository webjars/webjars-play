package org.webjars.requirejs

import org.webjars.play.RequireJS
import play.api.mvc.Call
import play.api.test.{WithApplication, PlaySpecification}

class RequireJSSpec extends PlaySpecification {

  "The setup" should {
    "produce the requirejs setup" in new WithApplication {
      val requireJs = app.injector.instanceOf[RequireJS]

      val javaScript = requireJs.setup(Call("GET", "/assets/js/app"))

      javaScript must contain("return ['/webjars/' + webJarId")
      javaScript must contain("\"requirejs\":\"2.1.20\"")
      javaScript must contain("""<script data-main="/assets/js/app" src="/webjars/requirejs/2.1.20/require.min.js"></script>""")
    }
  }

}
