package org.webjars.play

import play.api.http.MimeTypes
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class RequireJSSpec extends PlaySpecification {

  "RequireJS" should {
    "produce a setup" in new WithApplication {
      val requireJs = app.injector.instanceOf[RequireJS]

      val result = requireJs.setup()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome(MimeTypes.JAVASCRIPT)
      contentAsString(result) must contain("return ['/webjars/' + webJarId")
      contentAsString(result) must contain("\"requirejs\":\"2.3.3\"")
    }
    "produce a setup using a cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val requireJs = app.injector.instanceOf[RequireJS]

      val result = requireJs.setup()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome(MimeTypes.JAVASCRIPT)
      contentAsString(result) must contain("""["https://cdn.jsdelivr.net/webjars/react/0.12.2/react","/webjars/react/0.12.2/react","react"]""")
    }
  }

}
