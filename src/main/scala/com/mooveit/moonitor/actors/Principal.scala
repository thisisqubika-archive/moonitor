package com.mooveit.moonitor.actors

import akka.actor._
import com.mooveit.moonitor.actors.Agent.RetrieveStatus
import com.mooveit.moonitor.actors.Principal.Stop
import com.mooveit.moonitor.dto.AgentConfiguration

import scala.concurrent.duration._

class Principal(conf: AgentConfiguration) extends Actor {

  import context.dispatcher

  private var agent: ActorRef = _
  private var scheduledMessages: Cancellable = _

  override def preStart() = {
    agent = context.actorOf(Agent.props(conf.repository))
    scheduledMessages = context.system.scheduler.
      schedule(0.second, conf.frequency, agent, RetrieveStatus)
  }

  override def receive = {
    case Stop =>
      scheduledMessages.cancel()
      agent ! PoisonPill
  }
}

object Principal {

  def props(conf: AgentConfiguration) = Props(new Principal(conf))

  case object Stop
}
