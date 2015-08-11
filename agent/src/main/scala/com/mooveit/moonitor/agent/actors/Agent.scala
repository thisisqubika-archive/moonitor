package com.mooveit.moonitor.agent.actors

import akka.actor.{Actor, PoisonPill, Props}
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.agent.actors.Collector.ChangeFrequency
import com.mooveit.moonitor.domain.metrics.{Metric, MetricConfiguration, MetricValue}

class Agent(conf: Iterable[MetricConfiguration]) extends Actor {

  private var collectors = createCollectors(conf)

  def createCollectors(conf: Iterable[MetricConfiguration]) =
    conf.map(createCollectorMapping).toMap

  def createCollectorMapping(m: MetricConfiguration) =
    m.metric -> createCollectorActor(m)

  def createCollectorActor(conf: MetricConfiguration) =
    context.actorOf(Collector.props(conf))

  def stopAllCollectors() =
    collectors.values.foreach(_ ! PoisonPill)

  def resetCollectors(conf: Iterable[MetricConfiguration]) = {
    stopAllCollectors()
    collectors = createCollectors(conf)
  }

  def startCollecting(mconf: MetricConfiguration) = {
    collectors.get(mconf.metric) match {
      case Some(collector) =>
        collector ! ChangeFrequency(mconf.frequency)

      case None =>
        collectors += createCollectorMapping(mconf)
    }
  }

  def stopCollecting(metric: Metric) =
    collectors.get(metric).foreach(_ ! PoisonPill)

  def notifyPrincipal(mc: MetricCollected) = context.parent ! mc

  override def receive = {
    case mc: MetricCollected => notifyPrincipal(mc)

    case StartCollecting(mconf) => startCollecting(mconf)

    case StopCollecting(metric) => stopCollecting(metric)

    case ResetCollectors(mconfs) => resetCollectors(mconfs)

    case Stop =>
      stopAllCollectors()
      context.system.stop(self)
  }
}

object Agent {

  def props(configuration: Iterable[MetricConfiguration]) =
    Props(new Agent(configuration))

  case class MetricCollected(status: MetricValue)

  case class StartCollecting(metricConfiguration: MetricConfiguration)

  case class StopCollecting(metric: Metric)

  case class ResetCollectors(conf: Iterable[MetricConfiguration])

  case object Stop
}
