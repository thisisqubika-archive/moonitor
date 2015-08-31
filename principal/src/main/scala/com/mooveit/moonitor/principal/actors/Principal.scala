package com.mooveit.moonitor.principal.actors

import akka.actor._
import akka.remote.RemoteScope
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.mooveit.moonitor.principal.actors.Principal._
import com.mooveit.moonitor.principal.actors.Watcher._

class Principal(host: String, store: ActorRef, confStore: ActorRef)
  extends Actor {

  val config = context.system.settings.config
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
      val props = Agent.props(metricsConfig).withDeploy(deploy)
      agent = context.actorOf(props, s"agent-$host")

    case AlertsConfiguration(alertsConfig) =>
      watcher = context.actorOf(
        Watcher.props(host, alertsConfig, mailInformer), s"watcher-$host")

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

  def props(host: String, store: ActorRef, confStore: ActorRef) =
    Props(new Principal(host, store, confStore))

  case class MetricsConfiguration(conf: Iterable[MetricConfiguration])

  object MetricsConfiguration {

    def apply(confs: MetricConfiguration*): MetricsConfiguration = apply(confs)
  }

  case class AlertsConfiguration(conf: Iterable[AlertConfiguration])

  object AlertsConfiguration {

    def apply(confs: AlertConfiguration*): AlertsConfiguration = apply(confs)
  }
}
