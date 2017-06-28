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
  }

}
