package com.mooveit.moonitor.principal.actors

import akka.actor._
import akka.remote.RemoteScope
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.principal.actors.ConfigurationStore.Retrieve
import com.mooveit.moonitor.principal.actors.MetricsStore.Save

class Principal(host: String, repository: ActorRef, confRepository: ActorRef)
  extends Actor {

  private var agent: ActorRef = _

  override def preStart() = {
    confRepository ! Retrieve(host)
  }

  override def receive = {
    case Some(conf: Seq[MetricConfiguration]) =>
      val address = Address("akka.tcp", "agent-system", "127.0.0.1", 2552)
      val deploy = Deploy(scope = RemoteScope(address))
      agent = context.actorOf(Agent.props(conf).withDeploy(deploy))

    case MetricCollected(metricValue) =>
      repository ! Save(host, metricValue)
  }
}

object Principal {

  def props(host: String, repository: ActorRef, confRepository: ActorRef) =
    Props(new Principal(host, repository, confRepository))
}
