package com.mooveit.moonitor.principal.actors

import akka.actor._
import com.mooveit.moonitor.domain.metrics.{MaxFiles, MetricConfiguration}
import com.mooveit.moonitor.principal.actors.Agent.MetricCollected
import com.mooveit.moonitor.principal.actors.Repository.Save

import scala.concurrent.duration._

class Principal(host: String, repository: ActorRef) extends Actor {

  private var agent: ActorRef = _

  override def preStart() = {
    val conf = Seq(MetricConfiguration(MaxFiles, 2.seconds))
    agent = context.actorOf(Agent.props(conf), "agent")
    println(s"Principal: Created agent $agent")
  }

  override def receive = {
    case MetricCollected(timestamp, metricValue) =>
      println(s"Principal: Recieved MetricCollected($timestamp)")
      repository ! Save(host, timestamp, metricValue)
  }
}

object Principal {

  def props(host: String, repository: ActorRef) =
    Props(new Principal(host, repository))
}
