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
      var routes, script, webjarLoader;
    
      routes = ${Json.stringify(Json.toJson(routes))};
      
      function reversePath(n) {
        var comps, i, rn;
        comps = n.split("/");
        rn = "";
        for (i = comps.length - 1; i >= 0; --i) {
            if (rn.length > 0) {
                rn = rn.concat('/');
            }
            rn = rn.concat(comps[i]);
        }
        return rn;
      }

      function getFullPath(partialPath) {
        var p, rpp, route;
        rpp = reversePath(partialPath);
        for (p in routes) {
          if (routes.hasOwnProperty(p) && p.indexOf(rpp) === 0) {
            route = routes[p];
            break;
          } 
        }
        return route;
      }
    
      webjarLoader = function(name, req, onload, config) {
        req([getFullPath(name)], function(value) {
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
    
      script = document.createElement("script");
      script.setAttribute("type", "application/javascript");
      script.setAttribute("src", getFullPath("require.js"));
      document.getElementsByTagName("head")[0].appendChild(script);
    }());
            """
  }
}

