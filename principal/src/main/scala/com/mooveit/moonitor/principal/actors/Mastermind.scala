package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.agent.actors.Agent.Stop
import com.mooveit.moonitor.domain.alerts.{AlertConfiguration, Operator}
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricId}
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.Mastermind._

class Mastermind(store: ActorRef, confStore: ActorRef) extends Actor {

  private var principals = Map[String, ActorRef]()

  override def preStart() = {
    confStore ! RetrieveConfiguredHosts
  }

  override def postRestart(reason: Throwable) = {}

  def createPrincipalActor(host: String) =
    context.actorOf(Principal.props(host, store, confStore))

  def createPrincipalMapping(host: String) =
    host -> createPrincipalActor(host)

  override def receive = {
    case ConfiguredHosts(hosts) =>
      principals = hosts.map(createPrincipalMapping).toMap

    case StartHost(host) =>
      if (!principals.contains(host)) {
        principals += createPrincipalMapping(host)
        confStore ! SaveHost(host)
      }

    case StopHost(host) =>
      principals.get(host) foreach { principal =>
        principal ! Stop
        confStore ! RemoveHost(host)
      }
      principals -= host

    case StartCollecting(host, mconf) =>
      principals.get(host) foreach {
        _ ! Agent.StartCollecting(mconf)
      }

    case StopCollecting(host, metric) =>
      principals.get(host) foreach {
        _ ! Agent.StopCollecting(metric)
      }

    case StartWatching(host, aconf) =>
      principals.get(host) foreach {
        _ ! Watcher.StartWatching(aconf)
      }

    case StopWatching(host, metric) =>
      principals.get(host) foreach {
        _ ! Watcher.StopWatching(metric)
      }

    case GetPrincipals => sender() ! principals
  }
}

object Mastermind {

  def props(store: ActorRef, confStore: ActorRef) =
    Props(new Mastermind(store, confStore))
  
  case class ConfiguredHosts(hosts: Iterable[String])

  object ConfiguredHosts {

    def apply(hosts: String*): ConfiguredHosts = apply(hosts)
  }

  case class StartHost(host: String)

  case class StopHost(host: String)

  case class StartCollecting(host: String, mconf: MetricConfiguration)

  case class StopCollecting(host: String, metricId: MetricId)

  case class StartWatching(host: String, aconf: AlertConfiguration)

  case class StopWatching(host: String, metricId: MetricId)

  case object GetPrincipals
}
