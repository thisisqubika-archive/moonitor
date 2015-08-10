package com.mooveit.moonitor.hostconfig.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.hostconfig.actors.ConfigurationStore._
import redis.RedisClient

import com.mooveit.moonitor.domain.metrics.serialization.JacksonJsonSupport._

class ConfigurationStore extends Actor {

  import context.dispatcher

  private val repo = RedisClient()

  override def receive = {
    case SaveConfig(host, conf) => repo.set(host, conf)

    case Retrieve(host) =>
      val originalSender = sender()
      repo.get(host) map (originalSender ! _)
  }
}

object ConfigurationStore {

  case class SaveConfig(host: String, conf: Seq[MetricConfiguration])

  case class Retrieve(host: String)
}
