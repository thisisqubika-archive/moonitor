resolvers in ThisBuild  ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "rediscala" at "http://dl.bintray.com/etaty/maven")

val sprayVersion = "1.3.2"
val jacksonVersion = "2.6.0"
val rediscalaVersion = "1.4.0"
val scalajVersion = "1.1.5"
val akkaVersion = "2.1.4"
val akkaRemoteVersion = "2.3.12"
val akkaTestkitVersion = "2.3.11"
val scalatestVersion = "2.2.4"

lazy val commonSettings = Seq(
  organization := "com.mooveit",
  version := "0.1",
  scalaVersion := "2.11.7")

lazy val commonDependencies = Seq(
  "com.typesafe.akka" % "akka" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaRemoteVersion,
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.akka" %% "akka-testkit" % akkaTestkitVersion % "test",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test")

lazy val domain = (project in file("domain")).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % sprayVersion,
  "io.spray" %% "spray-http" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.etaty.rediscala" %% "rediscala" % rediscalaVersion)).
  settings(name := "moonitor-common")

lazy val agent = (project in file("agent")).
  dependsOn(domain).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= commonDependencies).
  settings(name := "moonitor-agent")

lazy val principal = (project in file("principal")).
  dependsOn(domain, agent).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= commonDependencies).
  settings(libraryDependencies ++= Seq(
  "com.etaty.rediscala" %% "rediscala" % rediscalaVersion,
  "org.scalaj" %% "scalaj-http" % scalajVersion)).
  settings(name := "moonitor-principal")

lazy val hostconfig = (project in file("hostconfig")).
  dependsOn(domain).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= commonDependencies).
  settings(libraryDependencies += "io.spray" %% "spray-can" % sprayVersion).
  settings(name := "moonitor-hostconfig")
