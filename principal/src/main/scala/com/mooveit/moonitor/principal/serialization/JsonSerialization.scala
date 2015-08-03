package com.mooveit.moonitor.principal.serialization

import akka.util.ByteString
import argonaut.Argonaut.{casecodec6, _}
import com.mooveit.moonitor.common.domain.{MachineStatus, PartitionStatus}
import redis.ByteStringFormatter

object JsonSerialization {

  implicit def PartitionStatusCodecJson =
    casecodec4(PartitionStatus.apply, PartitionStatus.unapply)(
      "fileSystemRoot",
      "totalSpace",
      "freeSpace",
      "usableSpace")

  implicit def MachineStatusCodecJson =
    casecodec6(MachineStatus.apply, MachineStatus.unapply)(
      "host",
      "availableProcessors",
      "freeMemory",
      "maxMemory",
      "totalMemory",
      "diskStatus")

  implicit val byteStringFormatter = new ByteStringFormatter[MachineStatus] {
    def serialize(data: MachineStatus) =
      ByteString(data.asJson.spaces2)

    def deserialize(bs: ByteString) =
      bs.utf8String.decodeOption[MachineStatus].orNull
  }
}
