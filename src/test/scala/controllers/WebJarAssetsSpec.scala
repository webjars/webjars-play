package controllers

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

object WebJarAssetsSpec extends PlaySpecification {

  "WebJarAssets" should {
    "be able to serve WebJar Assets" in new WithApplication {
      val requireJsPath = WebJarAssets.locate("require.js")
      requireJsPath must equalTo("requirejs/2.1.8/require.js")
      
      val result = controllers.WebJarAssets.at(requireJsPath)(FakeRequest())
    
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/javascript")
      contentAsString(result) must contain("RequireJS 2.1.8")
    }
  }

}