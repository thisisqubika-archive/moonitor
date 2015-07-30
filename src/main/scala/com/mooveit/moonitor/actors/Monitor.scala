package com.mooveit.moonitor.actors

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.actors.Monitor.Check
import com.mooveit.moonitor.actors.Notifier.FreeMemoryWarning
import com.mooveit.moonitor.dto.MachineStatus

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
