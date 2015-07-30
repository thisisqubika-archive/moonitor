package com.mooveit.moonitor.actors

import akka.actor.Actor
import com.mooveit.moonitor.actors.Notifier.FreeMemoryWarning
import com.mooveit.moonitor.dto.MachineStatus

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
