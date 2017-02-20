name := "sbt-scala-migrations"

organization := "com.github.dolcalmi"
description := "A wrapper over Scala Migrations (https://code.google.com/archive/p/scala-migrations/)"
homepage := Some(url("https://github.com/dolcalmi/scala-migrations-plugin"))

sbtPlugin := true

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
publishMavenStyle := false
bintrayRepository := "sbt-plugins"
bintrayOrganization := None
bintrayPackageLabels := Seq("sbt", "sbt-plugin", "pillar")
publishArtifact in Test := false
pomIncludeRepository := { _ => false}

libraryDependencies ++= Seq(
  "com.imageworks.scala-migrations" %% "scala-migrations" % "1.1.1",
  "com.typesafe" % "config" % "1.3.0",
  "mysql" % "mysql-connector-java" % "[5.1.0,5.2)" % "test",
  "org.apache.derby" % "derby" % "[10.5.3.0,11.0)" % "test",
  "postgresql" % "postgresql" % "9.1-901.jdbc4" % "test")
