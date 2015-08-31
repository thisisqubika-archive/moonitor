resolvers in ThisBuild ++= Seq(
  // Spray
  "Spray repository" at "http://repo.spray.io",
  // Rediscala
  "rediscala" at "http://dl.bintray.com/etaty/maven",
  // Courier
  "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"
)

val configVersion = "1.3.0"
val sprayVersion = "1.3.2"
val jacksonVersion = "2.6.0"
val rediscalaVersion = "1.4.0"
val scalajVersion = "1.1.5"
val sigarVersion = "1.6.4"
val courierVersion = "0.1.3"
val akkaVersion = "2.1.4"
val akkaRemoteVersion = "2.3.12"
val akkaTestkitVersion = "2.3.11"
val scalatestVersion = "2.2.4"
val logbackVersion = "1.1.3"

lazy val commonSettings = Seq(
  organization := "com.mooveit",
  version := "0.1",
  scalaVersion := "2.11.7")

lazy val commonDependencies = Seq(
  "com.typesafe.akka" % "akka" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaRemoteVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe" % "config" % configVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaTestkitVersion % "test",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test")

lazy val jacksonDependencies = Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion)

lazy val sprayDependencies = Seq(
  "io.spray" %% "spray-json" % sprayVersion,
  "io.spray" %% "spray-http" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-can" % sprayVersion)

lazy val rediscalaDependencies = Seq(
  "com.etaty.rediscala" %% "rediscala" % rediscalaVersion,
  "com.etaty.rediscala" %% "rediscala" % rediscalaVersion)

lazy val domainDependencies = Seq(
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion)

lazy val domain = (project in file("domain"))
  .settings(commonSettings: _*)
  .settings(
    name := "moonitor-common",
    libraryDependencies ++= domainDependencies
  )

lazy val collectorDependencies = Seq(
  "org.fusesource" % "sigar" % sigarVersion
)

lazy val collector = (project in file("collector"))
  .settings(commonSettings: _*)
  .settings(
    name := "moonitor-collector",
    libraryDependencies ++= collectorDependencies
  )
  .dependsOn(domain)

lazy val agentPackageSettings = Seq(
  bashScriptExtraDefines += """addJava "-Djava.library.path=${app_home}/../lib"""",
  maintainer in Linux := "Enrique Rodriguez <enrique.rodriguez@moove-it.com>",
  packageSummary in Linux := "Monitor agent.",
  packageDescription in Linux := "Monitor agent.",
  daemonUser in Linux := "mooveit",
  daemonGroup in Linux := (daemonUser in Linux).value,
  rpmVendor := "Moove-it",
  rpmLicense := Some("Apache License"),
  packageArchitecture in Rpm := "x86_64"
)

lazy val agentDependencies = commonDependencies :+ "org.fusesource" % "sigar" % sigarVersion

lazy val agent = (project in file("agent"))
  .settings(commonSettings: _*)
  .settings(
    name := "moonitor-agent",
    libraryDependencies ++= agentDependencies
  )
  .settings(agentPackageSettings: _*)
  .dependsOn(domain, collector)
  .enablePlugins(JavaServerAppPackaging)

lazy val principalPackageSettings = Seq(
  maintainer in Linux := "Enrique Rodriguez <enrique.rodriguez@moove-it.com>",
  packageSummary in Linux := "Monitor principal.",
  packageDescription in Linux := "Monitor principal.",
  daemonUser in Linux := "mooveit",
  daemonGroup in Linux := (daemonUser in Linux).value,
  rpmVendor := "Moove-it",
  rpmLicense := Some("Apache License"),
  packageArchitecture in Rpm := "x86_64"
)

lazy val principalDependencies =
  commonDependencies ++ jacksonDependencies ++
  sprayDependencies ++ rediscalaDependencies :+
  "org.scalaj" %% "scalaj-http" % scalajVersion :+
  "me.lessis" %% "courier" % courierVersion :+
  "io.spray" %% "spray-testkit" % sprayVersion % "test"

lazy val principal = (project in file("principal"))
  .settings(commonSettings: _*)
  .settings(
    name := "moonitor-principal",
    libraryDependencies ++= principalDependencies
  )
  .settings(principalPackageSettings: _*)
  .dependsOn(domain, agent)
  .enablePlugins(JavaServerAppPackaging)
