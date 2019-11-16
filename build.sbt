import xerial.sbt.Sonatype._

enablePlugins(play.sbt.routes.RoutesCompiler, GitVersioning, SbtTwirl)

organization := "org.webjars"

name := "webjars-play"

val Scala212 = "2.12.10"

scalaVersion := Scala212

crossScalaVersions := Seq(Scala212, "2.13.1")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation")

sources in (Compile, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Compile).value * "webjars.routes").get

sources in (Test, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Test).value * "routes").get

val playVersion = play.core.PlayVersion.current

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % playVersion % "provided",
  "org.webjars" % "requirejs" % "2.3.6",
  "org.webjars" % "webjars-locator" % "0.38",
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-specs2" % playVersion % "test",
  "org.webjars" % "bootstrap" % "3.1.0" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1" % "test"
)

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

publishTo := sonatypePublishToBundle.value

publishMavenStyle := true

sonatypeProjectHosting := Some(GitHubHosting("webjars", "webjars-play", "james@jamesward.com"))

javaOptions in Test := Seq("-Dlogger.resource=logback-test.xml")

fork in Test := true
