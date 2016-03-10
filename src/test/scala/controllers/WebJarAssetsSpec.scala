package controllers

import org.webjars.MultipleMatchesException
import play.api.test.{FakeRequest, WithApplication, PlaySpecification}

class WebJarAssetsSpec extends PlaySpecification {

  "WebJarAssets" should {
    "be able to serve WebJar Assets" in new WithApplication {
      val webJarAssets = app.injector.instanceOf[WebJarAssets]

      val requireJsPath = webJarAssets.locate("require.js")
      requireJsPath must equalTo("requirejs/2.1.20/require.js")
      
      val result = webJarAssets.at(requireJsPath)(FakeRequest())
    
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/javascript")
      contentAsString(result) must contain("RequireJS 2.1.20")
    }
    "be able to locate an asset with a webjar specified" in new WithApplication {
      val webJarAssets = app.injector.instanceOf[WebJarAssets]

      val bootstrapPath = webJarAssets.locate("bootswatch-yeti", "bootstrap.min.css")
      bootstrapPath must equalTo("bootswatch-yeti/3.1.1/css/bootstrap.min.css")
    }
    "get a MultipleMatchesException if there are multiple matches" in new WithApplication {
      val webJarAssets = app.injector.instanceOf[WebJarAssets]

      webJarAssets.locate("react.js") must throwA[MultipleMatchesException]
      webJarAssets.locate("react", "react.js") must throwA[MultipleMatchesException]
    }
    "be able to locate an asset which normally has multiple matches" in new WithApplication {
      val webJarAssets = app.injector.instanceOf[WebJarAssets]
      webJarAssets.fullPath("react", "react.js") must equalTo("react/0.12.2/react.js")
    }
  }

}
