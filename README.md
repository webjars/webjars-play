WebJars-Play
============

Introduction
------------
This project provides a Play framework controller so that developers using Play can easily access WebJars from their code.

In summary you need to configure a route in your Play application:

	# Enable webjar based resources to be returned
	GET     /webjars/*file              controllers.WebJarAssets.at(file)

...and then use WebJars within your views:

	<script type='text/javascript' src='@routes.WebJarAssets.at(WebJarAssets.locate("jquery.min.js"))'></script>

A user guide for this plugin can be found within the [WebJars Documentation](http://www.webjars.org/documentation).

Requirejs
---------
For single page JavaScript applications support has been provided for the popular [require.js](http://requirejs.org/) toolkit. The webjar-play controller can serve up a flavor of require.js so that the following types of expression will work from JavaScript:

	define(['webjars!angular.js'], 
	  function() {

The webjars! directive will cause require.js to locate WebJar resources on the server in a similar manner to calling WebJarAssets.locate within views. The difference is that this locating is all done in JavaScript on the client side.

To enable JavaScript WebJar loading declare require.js in a familiar way:

	<script data-main="js/app" src="lib/require.js"></script>

...and then configure some routes in your Play! application:

	# Obtain require.js with built-in knowledge of how webjars resources can be
	# resolved
	GET     /lib/require.js             controllers.WebJarAssets.requirejs()

	# Enable webjar based resources to be returned
	GET     /webjars/*file              controllers.WebJarAssets.at(file)

You're all set - no Play views required!

An example of the require.js support can be found in the [angular-seed-play project](https://github.com/huntc/angular-seed-play).


Releasing webjars-play
----------------------

1. Set the release version in `build.sbt`
2. Commit
3. Tag git `v2.x.y-z`
4. Push tags
5. Release: `sbt publish-signed`
6. Add `-SNAPSHOT` back to `build.sbt` version
