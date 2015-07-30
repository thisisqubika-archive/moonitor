package com.mooveit.moonitor.actors

import akka.actor.Actor
import com.mooveit.moonitor.actors.Repository.{Retrieve, Save}
import com.mooveit.moonitor.dto.MachineStatus

import scala.collection.immutable.TreeMap

class Repository extends Actor {

  private var repo = TreeMap[Long, MachineStatus]()

  override def receive = {
    case Save(timestamp, status) =>
      repo += timestamp -> status
      println(s"Saved: $timestamp - $status")

    case Retrieve(from, to) => repo.range(from, to)
  }
}

object Repository {

  case class Save(timestamp: Long, status: MachineStatus)

  case class Retrieve(from: Long, to: Long)
}