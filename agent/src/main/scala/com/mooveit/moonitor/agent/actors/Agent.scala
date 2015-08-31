package com.mooveit.moonitor.agent.actors

import akka.actor.{Actor, PoisonPill, Props}
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.agent.actors.Collector.ChangeFrequency
import com.mooveit.moonitor.domain.metrics._

class Agent(conf: Iterable[MetricConfiguration]) extends Actor {

  private var collectors = createCollectors(conf)

  def createCollectors(conf: Iterable[MetricConfiguration]) =
    conf.map(createCollectorMapping).toMap

  def createCollectorMapping(m: MetricConfiguration) =
    m.metricId -> createCollectorActor(m)

  def createCollectorActor(conf: MetricConfiguration) = {
    val id = s"collector-${conf.metricId.className}-${conf.metricId.params}"
    context.actorOf(Collector.props(conf), id)
  }

  private def stopAllCollectors() =
    collectors.values.foreach(_ ! PoisonPill)

  def resetCollectors(conf: Iterable[MetricConfiguration]) = {
    stopAllCollectors()
    collectors = createCollectors(conf)
  }

  def startCollecting(mconf: MetricConfiguration) = {
    collectors.get(mconf.metricId) match {
      case Some(collector) =>
        collector ! ChangeFrequency(mconf.frequency)

      case None =>
        collectors += createCollectorMapping(mconf)
    }
  }

  def stopCollecting(mid: MetricId) = {
    collectors.get(mid).foreach(_ ! PoisonPill)
    collectors -= mid
  }

  def notifyPrincipal(mc: MetricCollected) = context.parent ! mc

  override def receive = {
    case mc: MetricCollected => notifyPrincipal(mc)

    case StartCollecting(mconf) => startCollecting(mconf)

    case StopCollecting(mid) => stopCollecting(mid)

    case ResetCollectors(mconfs) => resetCollectors(mconfs)

    case GetCollectors => sender() ! collectors

    case StopAllCollectors => resetCollectors(Seq.empty)

    case Stop =>
      resetCollectors(Seq.empty)
      context stop self
  }
}

object Agent {

  def props(configuration: Iterable[MetricConfiguration]) =
    Props(new Agent(configuration))

  case class MetricCollected(id: MetricId, result: MetricResult)

  case class StartCollecting(configuration: MetricConfiguration)

  case class StopCollecting(id: MetricId)

  case class ResetCollectors(configuration: Iterable[MetricConfiguration])

  case object StopAllCollectors

  case object GetCollectors

  case object Stop
}
