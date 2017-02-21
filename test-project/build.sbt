name := "test-project"

version := "1.0"

scalaVersion := "2.12.1"

lazy val webJarsPlay = RootProject(file("..").getAbsoluteFile.toURI)

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(webJarsPlay)

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.4"

libraryDependencies += guice

routesGenerator := InjectedRoutesGenerator
