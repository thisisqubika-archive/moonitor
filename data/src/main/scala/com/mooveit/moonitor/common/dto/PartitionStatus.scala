package com.mooveit.moonitor.common.dto

case class PartitionStatus(fileSystemRoot: String,
                           totalSpace: Long,
                           freeSpace: Long,
                           usableSpace: Long)
