package com.mooveit.moonitor.common.domain

case class PartitionStatus(fileSystemRoot: String,
                           totalSpace: Long,
                           freeSpace: Long,
                           usableSpace: Long)
