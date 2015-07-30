package com.mooveit.moonitor.actors

import java.io.File

import akka.actor.{Actor, Cancellable, Props}
import com.mooveit.moonitor.actors.Agent.RetrieveStatus
import com.mooveit.moonitor.actors.Principal.{StatusUpdated, Stop}
import com.mooveit.moonitor.dto.{MachineStatus, PartitionStatus}

import scala.compat.Platform.currentTime
import scala.concurrent.Future
import scala.concurrent.duration._

class Agent(host: String, frequency: FiniteDuration) extends Actor {

  import context.dispatcher

  private var updateSchedule: Cancellable = _

  override def preStart() = {
    updateSchedule = context.system.scheduler.
      schedule(0.second, frequency, self, RetrieveStatus)
  }

  override def receive = {
    case RetrieveStatus => Future { retrieveInfo } map updatePrincipal

    case Stop =>
      updateSchedule.cancel()
      context stop self
  }

  def updatePrincipal(status: MachineStatus) =
    context.parent ! StatusUpdated(currentTime, status)

  def retrieveInfo: MachineStatus = {
    val processors = Runtime.getRuntime.availableProcessors()
    val memory = Runtime.getRuntime.freeMemory()
    val maxMemoryRaw = Runtime.getRuntime.maxMemory()
    val maxMemory = if (maxMemoryRaw == Long.MaxValue) -1L else maxMemoryRaw
    val totalMemory = Runtime.getRuntime.totalMemory()
    val diskStatus = File.listRoots() map createPartitionStatus

    MachineStatus(host, processors, memory, maxMemory, totalMemory, diskStatus.toList)
  }
  
  def createPartitionStatus(f: File) = PartitionStatus(
    f.getAbsolutePath, f.getTotalSpace, f.getFreeSpace, f.getUsableSpace
  )
}

object Agent {

  def props(host: String, frequency: FiniteDuration) =
    Props(new Agent(host, frequency))

  case object RetrieveStatus
}