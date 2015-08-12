package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricValue
import com.mooveit.moonitor.principal.actors.MetricsStore.Save

import scalaj.http.Http

class MetricsStore extends Actor {

  override def receive = {
    case Save(host, MetricValue(metric, timestamp, value)) =>
      Http("http://localhost:8086/write").
        params(("db", s"metrics-$host"), ("precision", "ms")).
        postData(s"${metric.getClass.getSimpleName} value=$value $timestamp").
        asBytes
  }
}

object MetricsStore {

  case class Save(host: String, status: MetricValue)

  case class MetricDocument(timestamp: String, value: Any)
}