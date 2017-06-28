package org.webjars.play

import org.webjars.MultipleMatchesException
import play.api.mvc.Call
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class WebJarsUtilSpec extends PlaySpecification {

  "WebJarsUtil" should {
    "locate an asset" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val requireJsPath = webJarsUtil.locate("require.js")
      requireJsPath must equalTo("requirejs/2.3.3/require.js")
    }
    "be able to locate an asset with a webjar specified" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val bootstrapPath = webJarsUtil.locate("bootswatch-yeti", "bootstrap.min.css")
      bootstrapPath must equalTo("bootswatch-yeti/3.1.1/css/bootstrap.min.css")
    }
    "get a MultipleMatchesException if there are multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      webJarsUtil.locate("react.js") must throwA[MultipleMatchesException]
      webJarsUtil.locate("react", "react.js") must throwA[MultipleMatchesException]
    }
    "be able to locate an asset which normally has multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.fullPath("react", "react.js") must equalTo("react/0.12.2/react.js")
    }
    "url" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.url("requirejs/2.3.3/require.js").url must beEqualTo("/webjars/requirejs/2.3.3/require.js")
      webJarsUtil.url("requirejs", "requirejs/2.3.3/require.js").url must beEqualTo("/webjars/requirejs/2.3.3/require.js")
      webJarsUtil.url("asdf1234qwer4321").url must throwA[IllegalArgumentException]
    }
    "generate a requireJs config" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val requireJs = webJarsUtil.requireJs(Call("GET", "/assets/js/app"))

      requireJs must contain("""<script src="/webjars/_requirejs"></script>""")
      requireJs must contain("""<script data-main="/assets/js/app" src="/webjars/requirejs/2.3.3/require.min.js"></script>""")
    }
  }

}
