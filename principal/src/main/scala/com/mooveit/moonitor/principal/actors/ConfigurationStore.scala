package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.{Metric, MetricConfiguration}
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.Mastermind.ConfiguredHosts
import com.mooveit.moonitor.principal.actors.Principal.{AlertsConfiguration, MetricsConfiguration}
import redis.RedisClient

import com.mooveit.moonitor.domain.serialization.JacksonJsonSupport._

class ConfigurationStore extends Actor {

  import context.dispatcher

  val CONFIGURED_HOSTS_KEY = "configured_hosts"
  private val repo = RedisClient()

  def makeMetricsKey(host: String) = s"${host}_metrics"

  def makeAlertsKey(host: String) = s"${host}_alerts"

  override def receive = {
    case RetrieveConfiguredHosts =>
      val originalSender = sender()
      val future = repo.hgetall[String](CONFIGURED_HOSTS_KEY)
      future map { conf => originalSender ! ConfiguredHosts(conf.keys) }

    case RetrieveMetricsConfig(host) =>
      val originalSender = sender()
      val future = repo.hgetall[MetricConfiguration](makeMetricsKey(host))
      future map { conf => originalSender ! MetricsConfiguration(conf.values) }

    case RetrieveAlertsConfig(host) =>
      val originalSender = sender()
      val future = repo.hgetall[AlertConfiguration](makeAlertsKey(host))
      future map { conf => originalSender ! AlertsConfiguration(conf.values) }

    case SaveHost(host) =>
      repo.hset(CONFIGURED_HOSTS_KEY, host, true)

    case RemoveHost(host) =>
      repo.hdel(CONFIGURED_HOSTS_KEY, host)
      repo.del(makeMetricsKey(host))

    case SaveMetric(host, mconf) =>
      repo.hset(makeMetricsKey(host), mconf.metric.toString, mconf)

    case RemoveMetric(host, metric) =>
      repo.hdel(makeMetricsKey(host), metric.toString)

    case SaveAlert(host, aconf) =>
      repo.hset(makeAlertsKey(host), aconf.metric.toString, aconf)

    case RemoveAlert(host, metric) =>
      repo.hdel(makeAlertsKey(host), metric.toString)
  }
}

object ConfigurationStore {

  case object RetrieveConfiguredHosts

  case class RetrieveMetricsConfig(host: String)

  case class RetrieveAlertsConfig(host: String)

  case class SaveHost(host: String)

  case class RemoveHost(host: String)

  case class SaveMetric(host: String, mconf: MetricConfiguration)

  case class RemoveMetric(host: String, metric: Metric)

  case class SaveAlert(host: String, aconf: AlertConfiguration)

  case class RemoveAlert(host: String, metric: Metric)
}
