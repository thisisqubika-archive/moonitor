package com.mooveit.moonitor.common.actors

import java.io.File

import akka.actor.{Actor, Cancellable, Props}
import com.mooveit.moonitor.common.actors.Agent.{RetrieveStatus, StatusUpdated}
import com.mooveit.moonitor.common.domain.{MachineStatus, PartitionStatus}

import scala.compat.Platform.currentTime
import scala.concurrent.Future
import scala.concurrent.duration._

class Agent(val host: String, val frequency: FiniteDuration) extends Actor {

  import context.dispatcher

  private var updateSchedule: Cancellable = _

  override def preStart() = {
    updateSchedule = context.system.scheduler.
      schedule(0.second, frequency, self, RetrieveStatus)
  }

  override def postStop() = {
    updateSchedule.cancel()
  }

  override def receive = {
    case RetrieveStatus =>
      println(s"Agent: Received RetrieveStatus from $sender")
      Future { retrieveInfo } map updatePrincipal
  }

  def updatePrincipal(status: MachineStatus) =
    context.parent ! StatusUpdated(currentTime, status)

  def retrieveInfo: MachineStatus = {
    val processors = Runtime.getRuntime.availableProcessors()
    val memory = Runtime.getRuntime.freeMemory()
    val maxMemoryRaw = Runtime.getRuntime.maxMemory()
    val maxMemory = if (maxMemoryRaw == Long.MaxValue) -1L else maxMemoryRaw
    val totalMemory = Runtime.getRuntime.totalMemory()
    val diskStatus = (File.listRoots() map createPartitionStatus).toList

    MachineStatus(host, processors, memory, maxMemory, totalMemory, diskStatus)
  }
  
  def createPartitionStatus(f: File) = PartitionStatus(
    f.getAbsolutePath, f.getTotalSpace, f.getFreeSpace, f.getUsableSpace
  )
}

object Agent {

  def props(host: String, frequency: FiniteDuration) =
    Props(new Agent(host, frequency))

  case object RetrieveStatus

  case class StatusUpdated(timestamp: Long, status: MachineStatus)
}