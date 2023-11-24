import xerial.sbt.Sonatype._

enablePlugins(play.sbt.routes.RoutesCompiler, GitVersioning, SbtTwirl)

organization := "org.webjars"

name := "webjars-play"

val Scala213 = "2.13.12"
val Scala3 = "3.3.1"

scalaVersion := Scala213

crossScalaVersions := Seq(Scala213, Scala3)

javacOptions ++= Seq("--release", "11")

scalacOptions ++= Seq("-release", "11","-unchecked", "-deprecation")

Compile / play.sbt.routes.RoutesKeys.routes / sources ++= ((Compile / unmanagedResourceDirectories).value * "webjars.routes").get

Test / play.sbt.routes.RoutesKeys.routes / sources ++= ((Test / unmanagedResourceDirectories).value * "routes").get

val playVersion = play.core.PlayVersion.current

resolvers += Resolver.mavenLocal

versionScheme := Some("semver-spec")

libraryDependencies ++= Seq(
  "org.playframework" %% "play" % playVersion % "provided",
  "org.webjars" % "requirejs" % "2.3.6",
  "org.webjars" % "webjars-locator" % "0.50",
  "org.playframework" %% "play-test" % playVersion % "test",
  "org.playframework" %% "play-specs2" % playVersion % "test",
  "org.webjars" % "bootstrap" % "3.4.1" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1+1" % "test"
)

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

sonatypeProjectHosting := Some(GitHubHosting("webjars", "webjars-play", "james@jamesward.com"))

Test / javaOptions := Seq("-Dlogger.resource=logback-test.xml")

Test / fork := true
