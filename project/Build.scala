import sbt._
import sbt.Keys._

object Resolvers {
  val resolvers = Seq(
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  )
}

object BuildSettings {
  import Resolvers._

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.webjars",
    name := "webjars-play",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.9.1",
    crossPaths := false,
    libraryDependencies += "play" %% "play" % "2.0.4" % "provided"
  )

  val sonatypeSettings = Seq(
    publishMavenStyle := true,
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
      <url>http://github.com/webjars/webjars-play</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://opensource.org/licenses/MIT</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
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
    )
  )
}

object WebJarsPlayBuild extends Build {
  import BuildSettings._

  lazy val root = Project(
    "webjars-play",
    file("."),
    settings = buildSettings ++ sonatypeSettings ++ Seq()
  )
}
