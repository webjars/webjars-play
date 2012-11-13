import sbt._
import sbt.Keys._

object BuildSettings {

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.webjars",
    name := "webjars-play",
    version := "2.1.0-SNAPSHOT",
    scalaVersion := "2.10.0-RC1",
    autoScalaLibrary := false,
    crossPaths := false,
    resolvers += Resolver.file("PlayIvy", file("/home/jamesw/projects/Play20/repository/local"))(Resolver.ivyStylePatterns),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % "2.10.0-RC1" % "provided",
      "play" %% "play" % "2.1-SNAPSHOT" % "provided",
      "org.reflections" % "reflections" % "0.9.8" % "provided",
      "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"
    ),
    licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/webjars/webjars-play"))
  )

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
