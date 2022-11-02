import xerial.sbt.Sonatype._

enablePlugins(play.sbt.routes.RoutesCompiler, GitVersioning, SbtTwirl)

organization := "org.webjars"

name := "webjars-play"

val Scala212 = "2.12.17"
val Scala213 = "2.13.10"
val Scala3 = "3.2.0"

scalaVersion := Scala213

crossScalaVersions := Seq(Scala212, Scala213, Scala3)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation")

Compile / play.sbt.routes.RoutesKeys.routes / sources ++= ((Compile / unmanagedResourceDirectories).value * "webjars.routes").get

Test / play.sbt.routes.RoutesKeys.routes / sources ++= ((Test / unmanagedResourceDirectories).value * "routes").get

val playVersion = play.core.PlayVersion.current

resolvers += Resolver.mavenLocal

versionScheme := Some("semver-spec")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % playVersion % "provided" cross CrossVersion.for3Use2_13,
  "org.webjars" % "requirejs" % "2.3.6",
  "org.webjars" % "webjars-locator" % "0.46" exclude("com.fasterxml.jackson.core", "jackson-databind"),
  "com.typesafe.play" %% "play-test" % playVersion % "test" cross CrossVersion.for3Use2_13,
  "com.typesafe.play" %% "play-specs2" % playVersion % "test" cross CrossVersion.for3Use2_13,
  "org.webjars" % "bootstrap" % "3.1.0" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1" % "test"
)

libraryDependencies ~= {
  _.map {
    case module if module.name == "twirl-api" =>
      module cross CrossVersion.for3Use2_13
    case module =>
      module
  }
}

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

publishTo := sonatypePublishToBundle.value

publishMavenStyle := true

sonatypeProjectHosting := Some(GitHubHosting("webjars", "webjars-play", "james@jamesward.com"))

Test / javaOptions := Seq("-Dlogger.resource=logback-test.xml")

Test / fork := true
