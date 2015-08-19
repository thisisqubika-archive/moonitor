package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricResult
import com.mooveit.moonitor.principal.Main
import com.mooveit.moonitor.principal.actors.MetricsStore.Save

import scalaj.http.Http

class MetricsStore extends Actor {

  val config = Main.config

  override def receive = {
    case Save(host, MetricResult(metric, timestamp, value)) =>
      Http(config.getString("influxdb.write_url")).
        params(("db", s"metrics-$host"), ("precision", "ms")).
        postData(s"$metric value=$value $timestamp").
        asBytes
  }
}

object MetricsStore {

  case class Save(host: String, status: MetricResult)

  case class MetricDocument(timestamp: String, value: Any)
}