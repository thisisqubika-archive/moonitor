package com.mooveit.moonitor.principal.actors

import akka.actor.{ActorLogging, Actor}
import com.mooveit.moonitor.domain.metrics.{MetricId, MetricResult}
import com.mooveit.moonitor.principal.actors.MetricsStore.Save

import scalaj.http.Http

class MetricsStore extends Actor with ActorLogging {

  val config = context.system.settings.config
  
  def sanitize(measurement: MetricId) =
    """(\.|=|,)""".r.replaceAllIn(measurement.toString, """\\$1""")

  override def receive = {
    case Save(host, metricId, MetricResult(timestamp, value)) =>
      val r =
        Http(config.getString("influxdb.write_url")).
          params(("db", s"metrics-$host"), ("precision", "ms")).
          postData(s"${sanitize(metricId)} value=$value $timestamp").
          asString

      log.debug(s"Response: ${r.code}. ${r.headers} || ${r.body}")
  }
}

object MetricsStore {

  case class Save(host: String, metricId: MetricId, metricResult: MetricResult)
}