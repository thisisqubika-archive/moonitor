package com.mooveit.moonitor.principal.actors

import akka.actor.Actor
import com.mooveit.moonitor.common.domain.MachineStatus
import com.mooveit.moonitor.principal.actors.Notifier.FreeMemoryWarning

class Notifier extends Actor {

  override def receive = {
    case FreeMemoryWarning(host, status) =>
      // Send mail to aluduena@moove-it.com
      println(s"Running out of memory on host $host!")
  }
}

object Notifier {

  case class FreeMemoryWarning(host: String, status: MachineStatus)
}
