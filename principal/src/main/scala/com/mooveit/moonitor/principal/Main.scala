package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.principal.actors._
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("principal-system")

  val store =
    system.actorOf(Props[MetricsStore], "metrics-store")
  val confStore =
    system.actorOf(Props[ConfigurationStore], "configuration-store")
  val mastermind =
    system.actorOf(Mastermind.props(store, confStore), "principal")

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10.seconds)
  val service = system.actorOf(ConfigurationService.props(mastermind))

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
