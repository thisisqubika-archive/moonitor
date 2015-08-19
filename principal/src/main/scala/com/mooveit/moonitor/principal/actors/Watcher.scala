package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.Metric
import com.mooveit.moonitor.principal.actors.MailInformer.Alert
import com.mooveit.moonitor.principal.actors.Watcher._

class Watcher(host: String,
              conf: Iterable[AlertConfiguration],
              informer: ActorRef)
  extends Actor {

  private var configuration = conf.map(aconf => aconf.metric -> aconf).toMap

  override def receive = {
    case StartWatching(aconf) =>
      configuration += aconf.metric -> aconf

    case StopWatching(metric) =>
      configuration -= metric

    case MetricCollected(metricResult) =>
      for (aconf <- configuration.get(metricResult.metric)) {
        if (aconf.operator.eval(metricResult.value, aconf.value)) {
          informer ! Alert(host, aconf)
        }
      }
  }
}

object Watcher {

  def props(host: String,
            conf: Iterable[AlertConfiguration],
            informer: ActorRef) =
    Props(new Watcher(host, conf, informer))

  case class StartWatching(aconf: AlertConfiguration)

  case class StopWatching(metric: Metric)
}
