enablePlugins(play.sbt.routes.RoutesCompiler, GitVersioning, SbtTwirl)

organization := "org.webjars"

name := "webjars-play"

val Scala213 = "2.13.16"
val Scala3 = "3.3.6"

scalaVersion := Scala213

crossScalaVersions := Seq(Scala213, Scala3)

javacOptions ++= Seq("--release", "17")

scalacOptions ++= Seq("-release", "17","-unchecked", "-deprecation")

Compile / play.sbt.routes.RoutesKeys.routes / sources ++= ((Compile / unmanagedResourceDirectories).value * "webjars.routes").get

Test / play.sbt.routes.RoutesKeys.routes / sources ++= ((Test / unmanagedResourceDirectories).value * "routes").get

val playVersion = play.core.PlayVersion.current

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.playframework" %% "play" % playVersion % "provided",
  "org.webjars" % "requirejs" % "2.3.7",
  "org.webjars" % "webjars-locator" % "0.52", // for RequireJS support
  "org.webjars" % "webjars-locator-lite" % "1.1.1",
  "org.playframework" %% "play-test" % playVersion % "test",
  "org.playframework" %% "play-specs2" % playVersion % "test",
  "org.webjars" % "bootstrap" % "3.1.1-2" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1+1" % "test"
)

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/webjars/webjars-play"))

developers := List(
  Developer(
    "jamesward",
    "James Ward",
    "james@jamesward.com",
    url("https://jamesward.com")
  )
)

versionScheme := Some("semver-spec")

Test / javaOptions := Seq("-Dlogger.resource=logback-test.xml")

Test / fork := true
