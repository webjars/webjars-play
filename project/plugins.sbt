addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.2")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.19")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.2")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")

// https://github.com/sbt/sbt/issues/7007
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)