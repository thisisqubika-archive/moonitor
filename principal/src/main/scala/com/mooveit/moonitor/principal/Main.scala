package com.mooveit.moonitor.principal

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.principal.actors._
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {

  val config = ConfigFactory.load()

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

  val interface = config.getString("restservice.iface")
  val port = config.getInt("restservice.port")
  IO(Http) ? Http.Bind(service, interface, port)
}
