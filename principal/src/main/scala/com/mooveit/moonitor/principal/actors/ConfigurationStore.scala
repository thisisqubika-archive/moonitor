package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.principal.actors.ConfigurationStore.Retrieve
import redis.RedisClient

import com.mooveit.moonitor.domain.metrics.serialization.JacksonJsonSupport._

class ConfigurationStore extends Actor {

  import context.dispatcher

  private val repo = RedisClient()

  override def receive = {
    case Retrieve(host) =>
      val originalSender = sender()
      repo.get(host)(byteStringFormatter) map { originalSender ! _ }
  }
}

object ConfigurationStore {

  case class Retrieve(host: String)
}
