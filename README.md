WebJars Play
============

A user guide for this plugin can be found within the [WebJars Documentation](http://www.webjars.org/documentation).


Releasing webjars-play
----------------------

1. Set the release version in `build.sbt`
2. Commit
3. Tag git `v2.x.y-z`
4. Push tags
5. Release: `activator +publish-signed`
6. Add `-SNAPSHOT` back to `build.sbt` version
7. Use the [Sonatype console](https://oss.sonatype.org/index.html#stagingRepositories) to release the staged artifacts
