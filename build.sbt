lazy val commonSettings = Seq(
  organization := "com.mooveit",
  version := "0.1",
  scalaVersion := "2.11.7")

lazy val commonDependencies = Seq(
    "com.typesafe.akka" % "akka" % "2.1.4",
    "com.typesafe.akka" %% "akka-remote" % "2.3.12",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test")

lazy val domain = (project in file("domain")).
  settings(commonSettings: _*).
  settings(name := "moonitor-common")

lazy val agent = (project in file("agent")).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= commonDependencies).
  settings(name := "moonitor-agent")

resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"
val sprayVersion = "1.3.2"

lazy val principal = (project in file("principal")).
  dependsOn(domain, agent).
  settings(commonSettings: _*).
  settings(libraryDependencies ++= commonDependencies).
  settings(name := "moonitor-principal")
