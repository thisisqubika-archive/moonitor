package com.mooveit.moonitor.principal.actors

import akka.actor._
import akka.remote.RemoteScope
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.typesafe.config.ConfigFactory

class Principal(host: String, store: ActorRef, confStore: ActorRef)
  extends Actor {

  val config = ConfigFactory.load()
  private var agent: ActorRef = _

  override def preStart() = {
    confStore ! RetrieveHostConfig(host)
  }

  override def postRestart(reason: Throwable) = {}

  override def receive = {
    case conf: Iterable[MetricConfiguration] =>
      val protocol = config.getString("agent.protocol")
      val systemName = config.getString("agent.system_name")
      val port = config.getInt("agent.port")
      val address = Address(protocol, systemName, host, port)
      val deploy = Deploy(scope = RemoteScope(address))
      agent = context.actorOf(Agent.props(conf).withDeploy(deploy))

    case start @ StartCollecting(m) =>
      confStore ! SaveHostMetric(host, m)
      agent ! start

    case stop @ StopCollecting(m) =>
      confStore ! RemoveHostMetric(host, m)
      agent ! stop

    case Stop => agent ! Stop

    case MetricCollected(metricValue) =>
      store ! Save(host, metricValue)
  }
}

object Principal {

  def props(host: String, repository: ActorRef, confRepository: ActorRef) =
    Props(new Principal(host, repository, confRepository))
}
