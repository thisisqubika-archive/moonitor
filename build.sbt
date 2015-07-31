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

lazy val data = (project in file("data")).
  settings(commonSettings: _*).
  settings(name := "moonitor-data")

lazy val principal = (project in file("principal")).
  dependsOn(data).
  settings(commonSettings: _*).
  settings(
    name := "moonitor-principal",
    libraryDependencies ++= Seq(
      "com.etaty.rediscala" %% "rediscala" % "1.4.0",
      "io.argonaut" %% "argonaut" % "6.0.4")
  )

lazy val agent = (project in file("agent")).
  dependsOn(data).
  settings(commonSettings: _*).
  settings(name := "moonitor-agent")
