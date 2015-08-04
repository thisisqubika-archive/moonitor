package controllers

import javax.inject.Inject

import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

class StatusController @Inject() (ws: WSClient) extends Controller {

  implicit val context =
    play.api.libs.concurrent.Execution.Implicits.defaultContext

  def all = Action.async {
    ws.url("http://localhost:8080/host_status/1/10").get().map { r =>
      Ok(r.body)
    }
  }
}
