package com.mooveit.moonitor.agent.actors

import akka.actor.{Actor, Cancellable, Props}
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricValue}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.agent.actors.Collector.{ChangeFrequency, Collect}
import com.mooveit.moonitor.agent.metrics.CollectionStrategyFactory._

import scala.compat.Platform._
import scala.concurrent.Future
import scala.concurrent.duration._

class Collector(conf: MetricConfiguration) extends Actor {

  import context.dispatcher

  private var scheduledCollection: Cancellable = _
  private val metric = conf.metric
  private val collectionStrategy = getCollectionStrategy(metric)

  override def preStart() = {
    scheduleCollection(conf.frequency)
  }

  override def postStop() = {
    scheduledCollection.cancel()
  }

  def scheduleCollection(frequency: Int) =
    scheduledCollection = context.system.scheduler.
      schedule(0.seconds, frequency.millis, self, Collect)

  def changeFrequency(newFrequency: Int) = {
    scheduledCollection.cancel()
    scheduleCollection(newFrequency)
  }

  def updateAgent(value: Any) =
    context.parent !
      MetricCollected(MetricValue(metric, currentTime, value))

  def collect() =
    Future { collectionStrategy.collect } map updateAgent

  override def receive = {
    case ChangeFrequency(newFrequency) => changeFrequency(newFrequency)

    case Collect => collect()
  }
}

object Collector {

  def props(conf: MetricConfiguration) = Props(new Collector(conf))

  case object Collect

  case class ChangeFrequency(newFrequency: Int)
}
