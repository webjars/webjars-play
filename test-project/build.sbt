name := "test-project"

version := "1.0"

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
//  "org.webjars" %% "webjars-play" % "2.8.8-1",
  "org.webjars" % "bootstrap" % "3.3.4"
)

lazy val webJarsPlay = RootProject(file("..").getAbsoluteFile.toURI)

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(webJarsPlay)

// enablePlugins(PlayScala)
