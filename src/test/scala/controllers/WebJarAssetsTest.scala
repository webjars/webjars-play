package controllers

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import play.api.test.WithApplication
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.mvc.{AnyContent, Action}

@RunWith(classOf[JUnitRunner])
class WebJarAssetsTest extends Specification {

  val RequireJsFile = "require.js"
  val RequireJsLocation = "requirejs/2.1.5/require.js"
  val RequireJsRoute = """{"require.js/2.1.5/requirejs/webjars/resources/META-INF":{"fullPath":"/webjars/requirejs/2.1.5/require.js","dependencies":["/webjars/requirejs/2.1.5/webjars-requirejs.js"]}}"""
  val SomeRequireJsAssetBody = "some body"

  object TestController extends WebJarAssets(new AssetsBuilder() {
    override def at(path: String, file: String): Action[AnyContent] = Action {
      request =>
        Ok(SomeRequireJsAssetBody)
    }
  })

  "The at method" should {
    "produce requirejs " in new WithApplication() {
      val result = TestController.at(RequireJsLocation)(FakeRequest())
      contentAsString(result) must_== SomeRequireJsAssetBody
    }
  }

  "The locate method" should {
    "locate requirejs" in new WithApplication() {
      val locatedFile = TestController.locate(RequireJsFile)
      locatedFile must_== RequireJsLocation
    }
  }

  "The requirejs method" should {
    "produce the JS with the routes for requirejs" in new WithApplication() {
      val result = TestController.requirejs()(FakeRequest())
      contentType(result) must beSome("text/javascript")
      contentAsString(result) must contain(RequireJsRoute)
    }
  }
}