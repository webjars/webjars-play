package org.webjars.play

import com.typesafe.config.ConfigFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Environment, Mode}
import play.api.mvc.Call
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

import scala.collection.JavaConverters._

class WebJarsUtilSpec extends PlaySpecification {

  def prodApp = {
    def loadConfiguration(env: Environment): Configuration = {
      val ourConfig = ConfigFactory.parseMap(Map("play.http.secret.key" -> "asdf1234").asJava)
      val defaultConfig = ConfigFactory.load()

      val config = ourConfig.withFallback(defaultConfig)
      Configuration(config)
    }

    GuiceApplicationBuilder(environment = Environment.simple(mode = Mode.Prod), loadConfiguration = loadConfiguration).build()
  }

  class WithProdApplication extends WithApplication(prodApp)

  "WebJarsUtil" should {
    "locate an asset" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val requireJsPath = webJarsUtil.locate("require.js")
      requireJsPath must beASuccessfulTry("requirejs/2.3.3/require.js")
    }
    "be able to locate an asset with a webjar specified" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val bootstrapPath = webJarsUtil.locate("bootswatch-yeti", "bootstrap.min.css")
      bootstrapPath must beASuccessfulTry("bootswatch-yeti/3.3.7/css/bootstrap.min.css")
    }
    "get a MultipleMatchesException if there are multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      webJarsUtil.locate("react.js") must beAFailedTry
      webJarsUtil.locate("react", "react.js") must beAFailedTry
    }
    "be able to locate an asset which normally has multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.fullPath("react", "react.js") must beASuccessfulTry("react/0.12.2/react.js")
    }
    "url" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.url("requirejs/2.3.3/require.js") must beASuccessfulTry("/webjars/requirejs/2.3.3/require.js")
      webJarsUtil.url("requirejs", "requirejs/2.3.3/require.js") must beASuccessfulTry("/webjars/requirejs/2.3.3/require.js")
      webJarsUtil.url("asdf1234qwer4321") must beAFailedTry
    }
    "url with a cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.url("require.js") must beASuccessfulTry("https://cdn.jsdelivr.net/webjars/org.webjars/requirejs/2.3.3/require.js")
    }
    "url with a custom cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true", "webjars.cdn-url" -> "http://asdf.com")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.url("require.js") must beASuccessfulTry("http://asdf.com/org.webjars/requirejs/2.3.3/require.js")
    }
    "generate a script tag from a partial WebJar path" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.script("jquery.js")
      script must beEqualTo("""<script src="/webjars/jquery/1.11.1/jquery.js" ></script>""")
    }
    "generate an error comment when the path isn't found" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.script("asdf1234")
      script must beEqualTo("""<!-- Could not get URL: asdf1234 could not be found. Make sure you've added the corresponding WebJar and please check for typos. -->""")
    }
    "generate an empty string when the path isn't found in prod mode" in new WithProdApplication {
      app.environment.mode must beEqualTo(Mode.Prod)
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.script("asdf1234")
      script must beEqualTo("")
    }
    "generate a script tag with a cdn url from a partial WebJar path" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.script("jquery.js")
      script must beEqualTo("""<script src="https://cdn.jsdelivr.net/webjars/org.webjars/jquery/1.11.1/jquery.js" ></script>""")
    }
    "generate a css tag from a partial WebJar path" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.css("bootswatch-yeti", "bootstrap.css")
      css must beEqualTo("""<link rel="stylesheet" type="text/css" href="/webjars/bootswatch-yeti/3.3.7/css/bootstrap.css">""")
    }
    "generate an error comment when the path isn't found" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.css("asdf1234")
      css must beEqualTo("""<!-- Could not get URL: asdf1234 could not be found. Make sure you've added the corresponding WebJar and please check for typos. -->""")
    }
    "generate an empty string when the path isn't found in prod mode" in new WithProdApplication {
      app.environment.mode must beEqualTo(Mode.Prod)
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.css("asdf1234")
      css must beEqualTo("")
    }
    "generate a css tag with a cdn url from a partial WebJar path" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.css("bootswatch-yeti", "bootstrap.css")
      css must beEqualTo("""<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/webjars/org.webjars/bootswatch-yeti/3.3.7/css/bootstrap.css">""")
    }
    "generate a requireJs config" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val requireJs = webJarsUtil.requireJs(Call("GET", "/assets/js/app"))

      requireJs must contain("""<script src="/webjars/_requirejs" ></script>""")
      requireJs must contain("""<script src="/webjars/requirejs/2.3.3/require.min.js" data-main="/assets/js/app"></script>""")
    }
    "generate a requireJs config with a cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val requireJs = webJarsUtil.requireJs(Call("GET", "/assets/js/app"))

      requireJs must contain("""<script src="/webjars/_requirejs" ></script>""")
      requireJs must contain("""<script src="https://cdn.jsdelivr.net/webjars/org.webjars/requirejs/2.3.3/require.min.js" data-main="/assets/js/app"></script>""")
    }
  }

}
