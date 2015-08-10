package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, Cancellable, Props}
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricValue}
import com.mooveit.moonitor.principal.actors.Agent.MetricCollected
import com.mooveit.moonitor.principal.actors.Collector.Collect
import com.mooveit.moonitor.principal.metrics.CollectionStrategyFactory._

import scala.compat.Platform._
import scala.concurrent.Future
import scala.concurrent.duration._

class Collector(conf: MetricConfiguration) extends Actor {

  import context.dispatcher

  private var scheduledRetrieve: Cancellable = _
  private val metric = conf.metric
  private val collectionStrategy = getCollectionStrategy(metric)

  override def preStart() = {
    scheduledRetrieve = context.system.scheduler.
      schedule(0.seconds, conf.frequency.millis, self, Collect)
  }

  override def postStop() = {
    scheduledRetrieve.cancel()
  }

  override def receive = {
    case Collect =>
      Future { collectionStrategy.collect } map updateAgent
  }

  def updateAgent(value: Any) =
    context.parent !
      MetricCollected(MetricValue(metric, currentTime, value))
}

object Collector {

  def props(conf: MetricConfiguration) = Props(new Collector(conf))

  case object Collect
}
