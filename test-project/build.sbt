name := "test-project"

version := "1.0"

scalaVersion := "2.11.8"

lazy val webJarsPlay = file("..").getAbsoluteFile.toURI

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(webJarsPlay)

libraryDependencies += "org.webjars" % "bootstrap" % "3.3.4"

routesGenerator := InjectedRoutesGenerator