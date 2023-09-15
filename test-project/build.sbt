name := "test-project"

version := "1.0"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  guice,
//  "org.webjars" %% "webjars-play" % "2.8.8-1",
  "org.webjars" % "bootstrap" % "3.3.4"
)

// note that something withCoursier + jquery being transitive here but in the test scope in webJarPlay,
// causes this project to lose the transitive runtime dependency on jquery. So we disable Coursier.

useCoursier := false

lazy val webJarsPlay = RootProject(file("..").getAbsoluteFile.toURI)

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(webJarsPlay)

// enablePlugins(PlayScala)
