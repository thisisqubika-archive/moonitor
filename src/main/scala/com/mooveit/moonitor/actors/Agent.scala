package com.mooveit.moonitor.actors

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import com.mooveit.moonitor.actors.Agent.RetrieveStatus
import com.mooveit.moonitor.actors.Repository.Save
import com.mooveit.moonitor.dto.{MachineStatus, PartitionStatus}

import scala.compat.Platform.currentTime
import scala.concurrent.Future

class Agent(repository: ActorRef) extends Actor {

  import context.dispatcher

  override def receive = {
    case RetrieveStatus => Future { retrieveInfo } map (st => repository ! Save(currentTime, st))
  }

  def retrieveInfo: MachineStatus = {
    val processors = Runtime.getRuntime.availableProcessors()
    val memory = Runtime.getRuntime.freeMemory()
    val maxMemoryRaw = Runtime.getRuntime.maxMemory()
    val maxMemory = if (maxMemoryRaw == Long.MaxValue) -1L else maxMemoryRaw
    val totalMemory = Runtime.getRuntime.totalMemory()
    val roots = File.listRoots()

    MachineStatus(processors, memory, maxMemory, totalMemory, roots map createPartitionStatus)
  }
  
  def createPartitionStatus(f: File): PartitionStatus =
    PartitionStatus(f.getAbsolutePath, f.getTotalSpace, f.getFreeSpace, f.getUsableSpace)
}

object Agent {

  def props(repository: ActorRef) = Props(new Agent(repository))

  case object RetrieveStatus
}