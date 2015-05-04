package org.webjars.requirejs

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class RequireJSSpec extends PlaySpecification {

  "The setup" should {
    "produce the requirejs setup" in new WithApplication {
      val javaScript =  org.webjars.play.RequireJS.setup("js/app")

      javaScript must contain("return ['/webjars/' + webJarId")
      javaScript must contain("\"requirejs\":\"2.1.15\"")
      javaScript must contain("""<script data-main="/assets/js%2Fapp" src="/webjars/requirejs%2F2.1.15%2Frequire.min.js"></script>""")
    }
  }

}
