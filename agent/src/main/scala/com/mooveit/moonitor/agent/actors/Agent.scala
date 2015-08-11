package com.mooveit.moonitor.agent.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricValue}

class Agent(conf: Seq[MetricConfiguration]) extends Actor {

  private var collectors = Seq[ActorRef]()

  def createCollectorActor(conf: MetricConfiguration) =
    context.actorOf(Collector.props(conf))

  override def preStart() = {
    collectors = conf map createCollectorActor
  }

  override def receive = {
    case mc: MetricCollected => context.parent ! mc
  }
}

object Agent {

  def props(configuration: Seq[MetricConfiguration]) =
    Props(new Agent(configuration))

  case class MetricCollected(status: MetricValue)
}
