resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

val akkaRemote = "com.typesafe.akka" %% "akka-remote" % "2.3.12"

lazy val commonSettings = Seq(
  organization := "com.mooveit",
  version := "0.1",
  scalaVersion := "2.11.7",
  libraryDependencies += akkaRemote
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
  settings(
    name := "moonitor-agent",
    libraryDependencies += akkaRemote
  )
