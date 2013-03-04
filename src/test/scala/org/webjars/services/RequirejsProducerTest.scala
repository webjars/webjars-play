package org.webjars.services

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class RequirejsProducerTest extends Specification {

  object RequirejsProducer extends RequirejsProducer
  val routes = Map("a.js" -> "somepath/a.js", "b.js" -> "somepath/b.js")

  "The produce method" should {
    "produce the JS with the stringified routes" in {
      RequirejsProducer.produce(routes) must contain("""{"a.js":"somepath/a.js","b.js":"somepath/b.js"}""")
    }
  }

}
