package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.common.domain.MachineStatus
import com.mooveit.moonitor.principal.actors.Repository.{Retrieve, Save}
import redis.RedisClient

import com.mooveit.moonitor.principal.serialization.JsonSerialization._

class Repository extends Actor {

  import context.dispatcher

  private var repo: RedisClient = _

  override def preStart() = {
    repo = RedisClient()
  }

  override def receive = {
    case Save(timestamp, status) =>
      val push = repo.rpush(s"status_${status.host}", status)
      push map (size => println(s"List size: $size"))

    case Retrieve(host, from, to) => repo.lrange(host, from, to)
  }
}

object Repository {

  case class Save(timestamp: Long, status: MachineStatus)

  case class Retrieve(host: String, from: Int, to: Int)
}