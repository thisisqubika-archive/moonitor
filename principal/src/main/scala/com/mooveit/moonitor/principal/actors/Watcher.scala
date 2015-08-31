package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.MetricId
import com.mooveit.moonitor.principal.actors.MailInformer._
import com.mooveit.moonitor.principal.actors.Watcher._

class Watcher(host: String,
              conf: Iterable[AlertConfiguration],
              informer: ActorRef)
  extends Actor {

  private var configuration = conf.map(aconf => aconf.metricId -> aconf).toMap
  private var alerted = Set[MetricId]()

  override def receive = {
    case StartWatching(aconf) =>
      configuration += aconf.metricId -> aconf

    case StopWatching(metric) =>
      configuration -= metric

    case MetricCollected(metricId, result) =>
      for (aconf <- configuration.get(metricId)) {
        val evaluated = aconf.operator.eval(result.value, aconf.value)
        lazy val alreadyAlerted = alerted.contains(metricId)

        if (evaluated && !alreadyAlerted) {
          informer ! Alert(host, aconf)
          alerted += metricId
        } else if (!evaluated && alreadyAlerted) {
          informer ! AlertCleared(host, aconf)
          alerted -= metricId
        }
      }

    case GetConfiguration => sender() ! configuration
  }
}

object Watcher {

  def props(host: String,
            conf: Iterable[AlertConfiguration],
            informer: ActorRef) =
    Props(new Watcher(host, conf, informer))

  case class StartWatching(aconf: AlertConfiguration)

  case class StopWatching(metricId: MetricId)

  case object GetConfiguration
}
