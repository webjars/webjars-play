enablePlugins(play.sbt.routes.RoutesCompiler, SbtTwirl)

organization := "org.webjars"

name := "webjars-play"

val Scala213 = "2.13.18"
val Scala33LTS = "3.3.8"
val Scala39LTS = "3.9.0"
val Scala3Next = "3.10.0-RC2"

val ScalaVersionAliases = Map(
  "2.13.x" -> Scala213,
  "3.3.x"  -> Scala33LTS,
  "3.9.x"  -> Scala39LTS,
  "3.next" -> Scala3Next
)

def resolveScalaVersion(version: String): String = ScalaVersionAliases.getOrElse(version, version)

scalaVersion := resolveScalaVersion(sys.props.getOrElse("scala.version", Scala213))

crossScalaVersions := Seq(Scala213, Scala33LTS)

javacOptions ++= Seq("--release", "17")

scalacOptions ++= Seq("-release", "17", "-unchecked", "-deprecation") ++
  (if (scalaVersion.value.startsWith("3.3.")) Seq("-Yfuture-lazy-vals") else Seq.empty)

Compile / play.sbt.routes.RoutesKeys.routes / sources ++= ((Compile / unmanagedResourceDirectories).value * "webjars.routes").get()

Test / play.sbt.routes.RoutesKeys.routes / sources ++= ((Test / unmanagedResourceDirectories).value * "routes").get()

val playVersion = play.core.PlayVersion.current

resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeCentralSnapshots

libraryDependencies ++= Seq(
  "org.playframework" %% "play" % playVersion % "provided",
  "org.webjars" % "requirejs" % "2.3.7",
  "org.webjars" % "webjars-locator" % "0.52", // for RequireJS support
  "org.webjars" % "webjars-locator-lite" % "1.1.2",
  "org.playframework" %% "play-test" % playVersion % "test",
  "org.playframework" %% "play-specs2" % playVersion % "test",
  "org.webjars" % "bootstrap" % "3.4.1" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1+1" % "test"
)

licenses := Seq("MIT License" -> uri("http://opensource.org/licenses/MIT"))

homepage := Some(uri("https://github.com/webjars/webjars-play"))

developers := List(
  Developer(
    "jamesward",
    "James Ward",
    "james@jamesward.com",
    uri("https://jamesward.com")
  )
)

versionScheme := Some("semver-spec")

Test / javaOptions := Seq("-Dlogger.resource=logback-test.xml")

Test / fork := true
