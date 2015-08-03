package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.common.domain.AgentConfiguration

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

  override def receive = ???
}
