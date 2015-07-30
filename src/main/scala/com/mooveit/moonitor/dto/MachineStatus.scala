package com.mooveit.moonitor.dto

case class MachineStatus(host: String,
                         availableProcessors: Int,
                         freeMemory: Long,
                         maxMemory: Long,
                         totalMemory: Long,
                         diskStatus: List[PartitionStatus])
