package com.mooveit.moonitor.dto

case class MachineStatus(availableProcessors: Int,
                         freeMemory: Long,
                         maxMemory: Long,
                         totalMemory: Long,
                         diskStatus: Seq[PartitionStatus])
