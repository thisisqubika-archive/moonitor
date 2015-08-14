package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.{Metric, MetricConfiguration}
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import redis.RedisClient

import com.mooveit.moonitor.domain.metrics.serialization.JacksonJsonSupport._

class ConfigurationStore extends Actor {

  import context.dispatcher

  val CONFIGURED_HOSTS_KEY = "configured_hosts"
  private val repo = RedisClient()

  override def receive = {
    case RetrieveConfiguredHosts =>
      val originalSender = sender()
      repo.hgetall[String](CONFIGURED_HOSTS_KEY) map { originalSender ! _.keys }

    case RetrieveHostConfig(host) =>
      val originalSender = sender()
      repo.hgetall[MetricConfiguration](host) map { originalSender ! _.values }

    case SaveHost(host) =>
      repo.hset(CONFIGURED_HOSTS_KEY, host, true)

    case RemoveHost(host) =>
      repo.hdel(CONFIGURED_HOSTS_KEY, host)
      repo.del(host)

    case SaveHostMetric(host, mconf) =>
      repo.hset(host, mconf.metric.toString, mconf)

    case RemoveHostMetric(host, metric) =>
      repo.hdel(host, metric.toString)
  }
}

object ConfigurationStore {

  case object RetrieveConfiguredHosts

  case class RetrieveHostConfig(host: String)

  case class SaveHost(host: String)

  case class RemoveHost(host: String)

  case class SaveHostMetric(host: String, mconf: MetricConfiguration)

  case class RemoveHostMetric(host: String, metric: Metric)
}
