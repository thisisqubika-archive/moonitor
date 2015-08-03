package com.mooveit.moonitor.principal.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.common.domain.MachineStatus
import com.mooveit.moonitor.principal.actors.Monitor.Check
import com.mooveit.moonitor.principal.actors.Notifier.FreeMemoryWarning

class Monitor(notifier: ActorRef) extends Actor {

  val THRESHOLD = 1L

  override def receive = {
    case Check(host, status) =>
      if (status.freeMemory <= THRESHOLD)
        notifier ! FreeMemoryWarning(host, status)
  }
}

object Monitor {

  def props(notifier: ActorRef) = Props(new Monitor(notifier))

  case class Check(host: String, status: MachineStatus)
}
