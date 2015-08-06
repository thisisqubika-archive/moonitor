package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import com.mooveit.moonitor.principal.actors._

object Main extends App {

  val system = ActorSystem("moonitor-actor-system")

  val repository = system.actorOf(Props[Repository], "repository")

  system.actorOf(Principal.props("localhost", repository), "principal")
}
