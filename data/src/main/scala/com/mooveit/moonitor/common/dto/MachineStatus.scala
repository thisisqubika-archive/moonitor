package com.mooveit.moonitor.common.dto

case class MachineStatus(host: String,
                         availableProcessors: Int,
                         freeMemory: Long,
                         maxMemory: Long,
                         totalMemory: Long,
                         diskStatus: List[PartitionStatus])
