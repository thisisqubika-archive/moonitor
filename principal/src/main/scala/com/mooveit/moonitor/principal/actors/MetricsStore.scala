package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricValue
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.typesafe.config.ConfigFactory

import scalaj.http.Http

class MetricsStore extends Actor {

  val config = ConfigFactory.load()

  override def receive = {
    case Save(host, MetricValue(metric, timestamp, value)) =>
      Http(config.getString("influxdb.write_url")).
        params(("db", s"metrics-$host"), ("precision", "ms")).
        postData(s"$metric value=$value $timestamp").
        asBytes
  }
}

object MetricsStore {

  case class Save(host: String, status: MetricValue)

  case class MetricDocument(timestamp: String, value: Any)
}