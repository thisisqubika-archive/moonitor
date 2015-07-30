package com.mooveit.moonitor

import akka.actor.{ActorSystem, Props}
import com.mooveit.moonitor.actors.Mastermind

object Main extends App {

  val system = ActorSystem("moonitor-actor-system")

  val mastermind = system.actorOf(Props[Mastermind], "the-mastermind")
}
