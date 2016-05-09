import play.api.ApplicationLoader
import play.api.inject.guice.{GuiceApplicationLoader, GuiceApplicationBuilder}

class CustomApplicationLoader extends GuiceApplicationLoader {
  override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    super.builder(context).disableCircularProxies(false)
  }
}
