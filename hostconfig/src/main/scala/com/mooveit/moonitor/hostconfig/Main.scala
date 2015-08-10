package com.mooveit.moonitor.hostconfig

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.hostconfig.actors.ConfigurationStore
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("moonitor-crudservice-system")

  val repository = system.actorOf(Props[ConfigurationStore])

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10.seconds)
  val service = system.actorOf(CrudService.props(repository))

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
