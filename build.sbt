lazy val root = (project in file(".")).enablePlugins(play.sbt.routes.RoutesCompiler, GitVersioning)

organization := "org.webjars"

name := "webjars-play"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.11.8", "2.12.2")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation")

sources in (Compile, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Compile).value * "webjars.routes").get

sources in (Test, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Test).value * "routes").get

val playVer: String = "2.6.5"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % playVer % "provided",
  "org.webjars" % "requirejs" % "2.3.3",
  "org.webjars" % "webjars-locator" % "0.32-1",
  "com.typesafe.play" %% "play-test" % playVer % "test",
  "com.typesafe.play" %% "play-specs2" % playVer % "test",
  "org.webjars" % "bootstrap" % "3.3.7-1" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.3.7" % "test")

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://github.com/webjars/webjars-play"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

scmInfo := Some(
  ScmInfo(
    url("https://github.com/webjars/webjars-play"),
    "scm:git:https://github.com/webjars/webjars-play.git"
  )
)

developers := List(
  Developer(
    id    = "jamesward",
    name  = "James Ward",
    email = "james@jamesward.com",
    url   = url("http://jamesward.com")
  )
)

useGpg := true
