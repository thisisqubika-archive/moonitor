package com.mooveit.moonitor.principal.actors

import akka.actor._
import akka.remote.RemoteScope
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.principal.Main
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.mooveit.moonitor.principal.actors.Principal._
import com.mooveit.moonitor.principal.actors.Watcher._

class Principal(host: String, store: ActorRef, confStore: ActorRef)
  extends Actor {

  val config = Main.config
  private var agent: ActorRef = _
  private var watcher: ActorRef = _
  private val mailInformer = context.actorOf(Props[MailInformer])

  override def preStart() = {
    confStore ! RetrieveMetricsConfig(host)
    confStore ! RetrieveAlertsConfig(host)
  }

  override def postRestart(reason: Throwable) = {}

  def agentDeployConfig = {
    val protocol = config.getString("agent.protocol")
    val systemName = config.getString("agent.system_name")
    val port = config.getInt("agent.port")
    Deploy(scope = RemoteScope(Address(protocol, systemName, host, port)))
  }
  
  override def receive = {
    case MetricsConfiguration(metricsConfig) =>
      val deploy = agentDeployConfig
      agent = context.actorOf(Agent.props(metricsConfig).withDeploy(deploy))

    case AlertsConfiguration(alertsConfig) =>
      watcher = context.actorOf(Watcher.props(host, alertsConfig, mailInformer))

    case startCollecting @ StartCollecting(mconf) =>
      confStore ! SaveMetric(host, mconf)
      agent ! startCollecting

    case stopCollecting @ StopCollecting(m) =>
      confStore ! RemoveMetric(host, m)
      agent ! stopCollecting

    case startWatching @ StartWatching(aconf) =>
      confStore ! SaveAlert(host, aconf)
      watcher ! startWatching

    case stopWatching @ StopWatching(metric) =>
      confStore ! RemoveAlert(host, metric)
      watcher ! stopWatching

    case Stop =>
      agent ! Stop
      watcher ! PoisonPill
      context stop self

    case metricCollected @ MetricCollected(id, result) =>
      store ! Save(host, id, result)
      watcher ! metricCollected
  }
}

object Principal {

  def props(host: String, repository: ActorRef, confRepository: ActorRef) =
    Props(new Principal(host, repository, confRepository))

  case class MetricsConfiguration(conf: Iterable[MetricConfiguration])

  case class AlertsConfiguration(conf: Iterable[AlertConfiguration])
}
