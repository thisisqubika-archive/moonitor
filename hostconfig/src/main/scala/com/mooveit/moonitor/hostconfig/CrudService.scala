package com.mooveit.moonitor.hostconfig

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.mooveit.moonitor.domain.metrics.MetricConfiguration
import com.mooveit.moonitor.domain.metrics.serialization.JacksonJsonSupport._
import com.mooveit.moonitor.hostconfig.actors.ConfigurationStore._
import spray.http.MediaTypes.`application/json`
import spray.routing.HttpServiceActor

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class CrudService(repository: ActorRef) extends HttpServiceActor {

  import context.dispatcher
  implicit val timeout = Timeout(10.seconds)

  def crudService = respondWithMediaType(`application/json`) {
    path("hosts" / Segment) { host =>
      post {
        entity(as[Seq[MetricConfiguration]]) {
          conf =>
            repository ! SaveConfig(host, conf)
            complete { "Ok" }
        }
      } ~
      get {
        val future = repository ? Retrieve(host)
        onComplete(future.mapTo[Option[Seq[MetricConfiguration]]]) {
          case Success(value) => complete { value.get }
          case Failure(ex) => complete(s"$ex")
        }
      }
    }
  }

  override def receive = runRoute(crudService)
}

object CrudService {

  def props(repository: ActorRef) = Props(new CrudService(repository))
}
