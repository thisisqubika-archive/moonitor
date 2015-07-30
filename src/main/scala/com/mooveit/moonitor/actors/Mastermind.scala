package com.mooveit.moonitor.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.actors.Mastermind.{StopAgent, StopAll}
import com.mooveit.moonitor.actors.Principal.Stop
import com.mooveit.moonitor.dto.AgentConfiguration

import scala.concurrent.duration._

class Mastermind extends Actor {

  private var repository: ActorRef = _
  private var principals = Map[String, ActorRef]()
  private val configuredHosts = Map("localhost" -> 2.seconds)

  override def preStart() = {
    repository = context.actorOf(Props[Repository], "example-repo")

    for ((host, frequency) <- configuredHosts) {
      val configuration = AgentConfiguration(host, repository, frequency)
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
