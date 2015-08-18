package com.mooveit.moonitor.agent

import java.io.File

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Main extends App {

  val config =
    ConfigFactory
      .parseFile(new File("/etc/default/moonitor-agent.conf"))
      .withFallback(ConfigFactory.load("dev"))

  System.loadLibrary("sigar-amd64-linux")

  ActorSystem("agent-system", config)
}
