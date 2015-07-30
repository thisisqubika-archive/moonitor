package com.mooveit.moonitor.dto

case class PartitionStatus(fileSystemRoot: String,
                           totalSpace: Long,
                           freeSpace: Long,
                           usableSpace: Long)
