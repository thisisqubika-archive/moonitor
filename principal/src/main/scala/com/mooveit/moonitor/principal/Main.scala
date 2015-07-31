package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import com.mooveit.moonitor.principal.actors.Mastermind

object Main extends App {

  val system = ActorSystem("principal-system")

  val mastermind = system.actorOf(Props[Mastermind], "the-mastermind")
}
