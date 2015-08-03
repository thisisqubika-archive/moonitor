resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

lazy val commonSettings = Seq(
  organization := "com.mooveit",
  version := "0.1",
  scalaVersion := "2.11.7",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka" % "2.1.4",
    "com.typesafe.akka" %% "akka-remote" % "2.3.12",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test")
)

lazy val common = (project in file("common")).
  settings(commonSettings: _*).
  settings(name := "moonitor-common")

lazy val principal = (project in file("principal")).
  dependsOn(common).
  settings(commonSettings: _*).
  settings(
    name := "moonitor-principal",
    libraryDependencies ++= Seq(
      "com.etaty.rediscala" %% "rediscala" % "1.4.0",
      "io.argonaut" %% "argonaut" % "6.0.4")
  )

lazy val agent = (project in file("agent")).
  dependsOn(common).
  settings(commonSettings: _*).
  settings(name := "moonitor-agent")
