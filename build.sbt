lazy val root = (project in file(".")).
    settings(
        organization := "com.mooveit",
        name := "moonitor",
        version := "0.1",
        scalaVersion := "2.11.7",
        libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.12"
    )


