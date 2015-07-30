package com.mooveit.moonitor.actors

import akka.actor._
import com.mooveit.moonitor.actors.Monitor.Check
import com.mooveit.moonitor.actors.Principal.{StatusUpdated, Stop}
import com.mooveit.moonitor.actors.Repository.Save
import com.mooveit.moonitor.dto.{AgentConfiguration, MachineStatus}

class Principal(conf: AgentConfiguration) extends Actor {

  private var agent: ActorRef = _

  override def preStart() = {
    agent = context.actorOf(Agent.props(conf.host, conf.frequency))
  }

  override def receive = {
    case StatusUpdated(timestamp, status) =>
      conf.repository ! Save(timestamp, status)
      conf.monitor ! Check(conf.host, status)

    case Stop =>
      agent ! Stop
      context stop self
  }
}

object Principal {

  def props(conf: AgentConfiguration) = Props(new Principal(conf))

  case class StatusUpdated(timestamp: Long, status: MachineStatus)

  case object Stop
}
