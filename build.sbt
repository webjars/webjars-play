lazy val root = (project in file(".")).enablePlugins(play.sbt.routes.RoutesCompiler)

organization := "org.webjars"

name := "webjars-play"

scalaVersion := "2.11.8"

version := "2.5.0-1"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation")

sources in (Test, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Test).value * "routes").get

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.5.0" % "provided",
  "org.webjars" % "requirejs" % "2.1.20",
  "org.webjars" % "webjars-locator" % "0.31",
  "com.typesafe.play" %% "play-test" % "2.5.0" % "test",
  "com.typesafe.play" %% "play-specs2" % "2.5.0" % "test",
  "org.webjars" % "bootstrap" % "3.1.0" % "test",
  "org.webjars" % "react" % "0.12.2" % "test",
  "org.webjars" % "bootswatch-yeti" % "3.1.1" % "test")

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

homepage := Some(url("http://github.com/webjars/webjars-play"))

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

pomExtra :=
  <scm>
    <connection>scm:git:https://github.com/webjars/webjars-play.git</connection>
    <developerConnection>scm:git:https://github.com/webjars/webjars-play.git</developerConnection>
    <url>git@github.com:webjars/webjars-play.git</url>
  </scm>
  <developers>
    <developer>
      <id>jamesward</id>
      <name>James Ward</name>
      <email>james@jamesward.com</email>
    </developer>
  </developers>
  <parent>
    <groupId>org.sonatype.oss</groupId>
    <artifactId>oss-parent</artifactId>
    <version>7</version>
  </parent>
