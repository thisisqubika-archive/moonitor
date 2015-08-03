package com.mooveit.moonitor.principal.actors

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.common.domain.MachineStatus
import com.mooveit.moonitor.principal.actors.Repository.Retrieve
import com.mooveit.moonitor.principal.serialization.JsonProtocol._
import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing.HttpServiceActor
import spray.util._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class HostStatusService(repository: ActorRef) extends HttpServiceActor {

  import context.dispatcher
  implicit val timeout = Timeout(5.seconds)

  def hostStatus(repository: ActorRef) =
    path("host_status" / IntNumber / IntNumber) { (from, to) =>
      get {
        respondWithMediaType(`application/json`) {
          onComplete((repository ? Retrieve("localhost", from, to)).mapTo[Seq[MachineStatus]]) {
            case Success(value) => complete { value }
            case Failure(ex) => complete(s"$ex")
          }
        }
      }
    }

  def receive = runRoute(hostStatus(repository))
}

object HostStatusService {

  def props(repository: ActorRef) =
    Props(new HostStatusService(repository))
}
