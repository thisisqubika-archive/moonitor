package com.mooveit.moonitor.agent.actors

import akka.actor.{Actor, Cancellable, Props}
import com.mooveit.moonitor.agent.CollectionStrategyLoader
import com.mooveit.moonitor.domain.metrics._
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.agent.actors.Collector.{GetFrequency, ChangeFrequency, Collect}

import scala.concurrent.Future
import scala.concurrent.duration._

class Collector(conf: MetricConfiguration) extends Actor {

  import context.dispatcher

  private var frequency = conf.frequency
  private var scheduledCollection: Cancellable = _
  private val collectionStrategy =
    CollectionStrategyLoader.
      loadCollectionStrategy(conf.metricId, conf.packageName)

  override def preStart() = {
    scheduleCollection()
  }

  override def postStop() = {
    scheduledCollection.cancel()
  }

  def scheduleCollection() =
    scheduledCollection = context.system.scheduler.
      schedule(0.seconds, frequency.millis, self, Collect)

  def changeFrequency(newFrequency: Int) = {
    scheduledCollection.cancel()
    frequency = newFrequency
    scheduleCollection()
  }

  def updateAgent(result: MetricResult) =
    context.parent !
      MetricCollected(conf.metricId, result)

  override def receive = {
    case ChangeFrequency(newFrequency) => changeFrequency(newFrequency)

    case Collect => Future { collectionStrategy.collect } map updateAgent

    case GetFrequency => sender() ! frequency
  }
}

object Collector {

  def props(conf: MetricConfiguration) = Props(new Collector(conf))

  case object Collect

  case class ChangeFrequency(newFrequency: Int)

  case object GetFrequency
}
