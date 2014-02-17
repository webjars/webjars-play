import sbt._
import sbt.Keys._

object BuildSettings {

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.webjars",
    name := "webjars-play",
    scalaVersion := "2.10.3",
    version := "2.2.1-1-SNAPSHOT",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.2.1" % "provided",
      "org.webjars" % "requirejs" % "2.1.8" % "test",
      "org.webjars" % "webjars-locator" % "0.10",
      "org.specs2" %% "specs2" % "2.3.3" % "test",
      "com.typesafe.play" %% "play-test" % "2.2.1" % "test"),
    licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/webjars/webjars-play")),
    unmanagedResourceDirectories in Compile <+= baseDirectory { _ / "conf" / "resources" })

  val sonatypeSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    pomExtra := (
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
      </parent>))
}

object WebJarsPlayBuild extends Build {
  import BuildSettings._

  lazy val root = Project(
    "webjars-play",
    file("."),
    settings = buildSettings ++ sonatypeSettings ++ Seq())
}
