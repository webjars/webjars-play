package org.webjars.services

import play.api.libs.json._

/**
 * Builds require.js bootstrap code whereby all of the webjar resources are
 * resolved and declared to a require.js loader. The real require.js is then
 * loaded taking into consideration this configuration.
 *
 */
trait RequirejsProducer {

  def produce(routes: Map[String, String]): String = {
    s"""
    var require;
    (function() {
      var routes = ${Json.stringify(Json.toJson(routes))};
      var webjarLoader = function(name, req, onload, config) {
        req([routes[name]], function(value) {
          onload(value);
        });
      }
      require = {
        callback: function() {
          define("webjars", function() {
            return {load: webjarLoader};
          });
        }
      };
      var script = document.createElement("script");
      script.setAttribute("type", "application/javascript");
      script.setAttribute("src", routes["require.js"]);
      document.getElementsByTagName("head")[0].appendChild(script);
    }());
            """
  }
}

