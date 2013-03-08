package org.webjars.services

import play.api.libs.json._

case class Route(fullPath: String, dependencies: List[String])

object RouteSerializer {
  implicit def routeWriter: Writes[Route] = Json.writes[Route]
}

/**
 * Builds require.js bootstrap code whereby all of the webjar resources are
 * resolved and declared to a require.js loader. The real require.js is then
 * loaded taking into consideration this configuration.
 *
 */
trait RequirejsProducer {

  import RouteSerializer._

  def produce(routes: Map[String, Route]): String = {
    s"""
    var require;
    (function(routes) {
      var rjsReverseFullPath, script;
    
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

      function getReverseFullPath(partialPath) {
        var p, reverseFullPath, reversePartialPath, route;
        reversePartialPath = reversePath(partialPath);
        for (p in routes) {
          if (routes.hasOwnProperty(p) && p.indexOf(reversePartialPath) === 0) {
            reverseFullPath = p;
            break;
          } 
        }
        return reverseFullPath;
      }
    
      function getDependencyFullPaths(route) {
        var dependencyFullPaths, dependencyRouteKey, i;
        dependencyFullPaths = new Array(route.dependencies.length);
        for (i = 0; i < route.dependencies.length; ++i) {
          dependencyRouteKey = getReverseFullPath(route.dependencies[i]);
          dependencyRoute = routes[dependencyRouteKey];
          if (dependencyRoute === undefined) {
              throw "No WebJar dependency found for " + route.dependencies[i] + 
                ". Please ensure that this is a valid dependency";
          } else {
            dependencyFullPaths.push(dependencyRoute.fullPath);
          }
        }
        return dependencyFullPaths;
      }
    
      function webjarLoader(name, req, onload, config) {
        var routeKey = getReverseFullPath(name);
        var route = routes[routeKey];
        if (route === undefined) {
          throw "No WebJar dependency found for " + name + 
            ". Please ensure that this is a valid dependency";
        }
        function mainLoader() {
          req([route.fullPath], onload);
        }
        req(getDependencyFullPaths(route), mainLoader);
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
      rjsReverseFullPath = getReverseFullPath("require.js");
      script.setAttribute("src", routes[rjsReverseFullPath].fullPath);
      document.getElementsByTagName("head")[0].appendChild(script);
    }(${Json.stringify(Json.toJson(routes))}));
            """
  }
}

