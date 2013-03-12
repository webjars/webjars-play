package controllers

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.http.ContentTypes._

@RunWith(classOf[JUnitRunner])
class WebJarAssetsTest extends Specification {

  val RequireJsRoute = """{"require.js/2.1.1/requirejs/webjars/resources/META-INF":{"fullPath":"/webjars/requirejs/2.1.1/require.js","dependencies":["/webjars/requirejs/2.1.1/webjars-requirejs.js"]}}"""

  object TestController extends WebJarAssets

  "The requirejs method" should {
    "produce the JS with the routes for requirejs" in new WithApplication() {
      val result = TestController.requirejs()(FakeRequest())
      contentType(result) must beSome("text/javascript")
      contentAsString(result) must contain(RequireJsRoute)
    }
  }
}