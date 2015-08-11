package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import com.mooveit.moonitor.principal.actors.Principal.props
import com.mooveit.moonitor.principal.actors._

object Main extends App {

  val system = ActorSystem("principal-system")

  val repository =
    system.actorOf(Props[MetricsStore], "metrics-store")
  val confRepository =
    system.actorOf(Props[ConfigurationStore], "configuration-store")

  system.actorOf(props("localhost", repository, confRepository), "principal")
}
