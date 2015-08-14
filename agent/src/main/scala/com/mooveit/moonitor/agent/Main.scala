package com.mooveit.moonitor.agent

import akka.actor.ActorSystem

object Main extends App {

  System.load(getClass.getClassLoader.
    getResource("libsigar-amd64-linux.so").getFile)

  val system = ActorSystem("agent-system")
}
