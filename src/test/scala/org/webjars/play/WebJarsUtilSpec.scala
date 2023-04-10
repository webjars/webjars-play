package org.webjars.play

import com.typesafe.config.ConfigFactory
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import play.api.test.{PlaySpecification, WithApplication}
import play.api.{Application, Configuration, Environment, Mode}

import scala.util.Try

class WebJarsUtilSpec extends PlaySpecification {

  def prodApp: Application = {
    def loadConfiguration(env: Environment): Configuration = {
      val ourConfig = ConfigFactory.parseString("play.http.secret.key=asdfghjk1234567890")
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
      requireJsPath.path must beASuccessfulTry("requirejs/2.3.6/require.js")
    }
    "be able to locate an asset with a webjar specified" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val bootstrapPath = webJarsUtil.locate("bootswatch-yeti", "bootstrap.min.css")
      bootstrapPath.path must beASuccessfulTry("bootswatch-yeti/3.3.7/css/bootstrap.min.css")
    }
    "get a MultipleMatchesException if there are multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      webJarsUtil.locate("foo.js").path must beAFailedTry
      webJarsUtil.locate("multiplematch", "react.js").path must beAFailedTry
    }
    "be able to locate an asset which normally has multiple matches" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.fullPath("react", "react.js").path must beASuccessfulTry("react/0.14.8/react.js")
    }
    "url" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.locate("requirejs/2.3.6/require.js").url must beASuccessfulTry("/requirejs/2.3.6/require.js")
      webJarsUtil.locate("requirejs", "requirejs/2.3.6/require.js").url must beASuccessfulTry("/requirejs/2.3.6/require.js")
      webJarsUtil.locate("asdf1234qwer4321").url must beAFailedTry
    }
    "url with a cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.locate("require.js").url must beASuccessfulTry("https://cdn.jsdelivr.net/webjars/org.webjars/requirejs/2.3.6/require.js")
    }
    "url with a custom cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true", "webjars.cdn-url" -> "http://asdf.com")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      webJarsUtil.locate("require.js").url must beASuccessfulTry("http://asdf.com/org.webjars/requirejs/2.3.6/require.js")
    }
    "generate a script tag from a partial WebJar path" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.locate("jquery.js").script()
      script.body.trim must beEqualTo("""<script src="/jquery/1.11.1/jquery.js" ></script>""")
    }
    "generate an error comment when the path isn't found" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = Try(webJarsUtil.locate("asdf1234").script())
      script must beAFailedTry
    }
    "generate an empty string when the path isn't found in prod mode" in new WithProdApplication {
      app.environment.mode must beEqualTo(Mode.Prod)
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.locate("asdf1234").script()
      script.body must beEqualTo("")
    }
    "generate a script tag with a cdn url from a partial WebJar path" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val script = webJarsUtil.locate("jquery.js").script()
      script.body.trim must beEqualTo("""<script src="https://cdn.jsdelivr.net/webjars/org.webjars/jquery/1.11.1/jquery.js" ></script>""")
    }
    "generate a css tag from a partial WebJar path" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.locate("bootswatch-yeti", "bootstrap.css").css()
      css.body.trim must beEqualTo("""<link rel="stylesheet" type="text/css" href="/bootswatch-yeti/3.3.7/css/bootstrap.css" >""")
    }
    "generate an error comment when the path isn't found" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = Try(webJarsUtil.locate("asdf1234").css())
      css must beAFailedTry
    }
    "generate an empty string when the path isn't found in prod mode" in new WithProdApplication {
      app.environment.mode must beEqualTo(Mode.Prod)
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.locate("asdf1234").css()
      css.body must beEqualTo("")
    }
    "generate a css tag with a cdn url from a partial WebJar path" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val css = webJarsUtil.locate("bootswatch-yeti", "bootstrap.css").css()
      css.body.trim must beEqualTo("""<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/webjars/org.webjars/bootswatch-yeti/3.3.7/css/bootstrap.css" >""")
    }
    "generate an img tag from a partial WebJar path" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val img = webJarsUtil.locate("bootswatch-yeti", "bootstrap.css").img()
      img.body.trim must beEqualTo("""<img src="/bootswatch-yeti/3.3.7/css/bootstrap.css" >""")
    }
    "generate an error comment when the path isn't found" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val img = Try(webJarsUtil.locate("asdf1234").img())
      img must beAFailedTry
    }
    "generate an empty string when the path isn't found in prod mode" in new WithProdApplication {
      app.environment.mode must beEqualTo(Mode.Prod)
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val img = webJarsUtil.locate("asdf1234").img()
      img.body must beEqualTo("")
    }
    "generate a css tag with a cdn url from a partial WebJar path" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val img = webJarsUtil.locate("bootswatch-yeti", "bootstrap.css").img()
      img.body.trim must beEqualTo("""<img src="https://cdn.jsdelivr.net/webjars/org.webjars/bootswatch-yeti/3.3.7/css/bootstrap.css" >""")
    }
    "generate a requireJs config" in new WithApplication {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]
      val requireJs = webJarsUtil.requireJs(Call("GET", "/assets/js/app"))

      requireJs.body must contain ("""<script src="/_requirejs" ></script>""")
      requireJs.body must contain("""<script src="/requirejs/2.3.6/require.min.js"  data-main="/assets/js/app" ></script>""")
    }
    "generate a requireJs config with a cdn" in new WithApplication(_.configure("webjars.use-cdn" -> "true")) {
      val webJarsUtil = app.injector.instanceOf[WebJarsUtil]

      val requireJs = webJarsUtil.requireJs(Call("GET", "/assets/js/app"))

      requireJs.body must contain("""<script src="/_requirejs" ></script>""")
      requireJs.body must contain("""<script src="https://cdn.jsdelivr.net/webjars/org.webjars/requirejs/2.3.6/require.min.js"  data-main="/assets/js/app" ></script>""")
    }
  }

}
