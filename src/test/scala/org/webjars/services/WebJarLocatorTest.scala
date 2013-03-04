package org.webjars.services

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class WebJarLocatorTest extends Specification {

  val REQUIREJS_PATH = "requirejs/2.1.1/require.js"

  object WebJarLocator extends WebJarLocator
  val webJarFilterExpr = """.*\.js$""".r
  val routes = WebJarLocator.locateAll(webJarFilterExpr, classOf[WebJarLocatorTest].getClassLoader())

  "The locate method" should {
    "return require.js" in {
      WebJarLocator.locate(routes, "require.js") mustEqual REQUIREJS_PATH
    }

    "not return sometother.js" in {
      WebJarLocator.locate(routes, "sometother.js") must throwA[NoSuchElementException]
    }

    "find require.js using its full path" in {
      WebJarLocator.locate(routes, REQUIREJS_PATH) mustEqual REQUIREJS_PATH
    }
  }

  "The locateAll method" should {
    "return all of the js routes" in {

      (routes.size mustEqual 1) and
        (routes must havePair("require.js" -> REQUIREJS_PATH))
    }
  }

}
