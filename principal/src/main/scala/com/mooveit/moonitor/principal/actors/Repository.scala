package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricValue
import com.mooveit.moonitor.principal.actors.Repository.Save

class Repository extends Actor {

  override def preStart() = {
  }

  override def receive = {
    case Save(host, timestamp, metricValue) =>
      println(s"Saved ($host, $timestamp, $metricValue")
  }
}

object Repository {

  case class Save(host: String, timestamp: Long, status: MetricValue)
}