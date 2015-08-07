package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.domain.metrics.MetricValue
import com.mooveit.moonitor.principal.actors.Repository.{MetricDocument, Save}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat.dateTime

class Repository extends Actor {

  private val elasticClient = ElasticClient.remote("localhost", 9300)

  override def receive = {
    case Save(host, MetricValue(metric, timestamp, value)) =>
      val indexName = s"metrics-$host"
      val indexType = metric.getClass.getSimpleName
      val document = MetricDocument(dateTime().print(timestamp), value)

      elasticClient execute {
        index into indexName / indexType source document
      }
  }
}

object Repository {

  case class Save(host: String, status: MetricValue)

  case class MetricDocument(timestamp: String, value: Any)
}