resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

lazy val root = (project in file(".")).
  settings(
      organization := "com.mooveit",
      name := "moonitor",
      version := "0.1",
      scalaVersion := "2.11.7",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % "2.3.12",
        "com.etaty.rediscala" %% "rediscala" % "1.4.0",
        "io.argonaut" %% "argonaut" % "6.0.4"
      )
  )


