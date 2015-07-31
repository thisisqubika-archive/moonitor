package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.common.dto.AgentConfiguration
import com.mooveit.moonitor.principal.actors.Mastermind.{StopAgent, StopAll}
import com.mooveit.moonitor.principal.actors.Principal.Stop

import scala.concurrent.duration._

class Mastermind extends Actor {

  private var repository: ActorRef = _
  private var monitor: ActorRef = _
  private var notifier: ActorRef = _
  private var principals = Map[String, ActorRef]()
  private val configuredHosts = Map("localhost" -> 2.seconds)

  override def preStart() = {
    repository = context.actorOf(Props[Repository], "repository")
    notifier = context.actorOf(Props[Notifier], "notifier")
    monitor = context.actorOf(Monitor.props(notifier), "monitor")

    for ((host, frequency) <- configuredHosts) {
      val configuration =
        AgentConfiguration(host, repository, frequency, monitor)
      principals += host -> context.actorOf(Principal.props(configuration))
    }
  }

  override def receive = {
    case StopAgent(host) => principals.get(host).foreach(_ ! Stop)

    case StopAll => principals.values.foreach(_ ! Stop)
  }
}

object Mastermind {

  case class StopAgent(host: String)

  case object StopAll
}
