package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.common.domain.AgentConfiguration
import com.mooveit.moonitor.principal.actors._
import scala.concurrent.duration._
import spray.can.Http

object Main extends App {

  val configuredHosts = Map("localhost" -> 2.seconds)

  implicit val system = ActorSystem("principal-system")
  val repository = system.actorOf(Props[Repository], "repository")
  val notifier = system.actorOf(Props[Notifier], "notifier")
  val monitor = system.actorOf(Monitor.props(notifier), "monitor")

  for ((host, frequency) <- configuredHosts) {
    val configuration = AgentConfiguration(host, repository, frequency, monitor)
    system.actorOf(Principal.props(configuration))
  }

  val service = system.actorOf(HostStatusService.props(repository))
  implicit val timeout = Timeout(5.seconds)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
