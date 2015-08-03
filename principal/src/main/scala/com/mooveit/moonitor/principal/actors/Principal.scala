package com.mooveit.moonitor.principal.actors

import akka.actor._
import com.mooveit.moonitor.common.actors.Agent
import com.mooveit.moonitor.common.actors.Agent.StatusUpdated
import com.mooveit.moonitor.common.domain.AgentConfiguration
import com.mooveit.moonitor.principal.actors.Monitor.Check
import com.mooveit.moonitor.principal.actors.Repository.Save

class Principal(conf: AgentConfiguration) extends Actor {

  private var agent: ActorRef = _

  override def preStart() = {
    agent = context.actorOf(Agent.props(conf.host, conf.frequency), "agent")
    println(s"Principal: Created agent $agent")
  }

  override def receive = {
    case StatusUpdated(timestamp, status) =>
      println(s"Principal: Recieved StatusUpdated($timestamp)")
      conf.repository ! Save(timestamp, status)
      conf.monitor ! Check(conf.host, status)
  }
}

object Principal {

  def props(conf: AgentConfiguration) = Props(new Principal(conf))
}
