package org.webjars.services

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope
import play.api.libs.json._
import play.api.test.WithApplication

@RunWith(classOf[JUnitRunner])
class RequirejsProducerTest extends Specification {

  object RequirejsProducer extends RequirejsProducer

  val routes = Map(
    "a.js" -> Route("somepath/a.js", List("b.js")),
    "b.js" -> Route("somepath/b.js", List()))

  "The produce method" should {
    "produce the JS with the stringified routes" in new WithApplication() {
      RequirejsProducer.produce(routes) must contain(
        """{"a.js":{"fullPath":"somepath/a.js","dependencies":["b.js"]},""" +
          """"b.js":{"fullPath":"somepath/b.js","dependencies":[]}}""")
    }
  }

}
