package com.mooveit.moonitor.principal.serialization

import com.mooveit.moonitor.common.domain.{MachineStatus, PartitionStatus}
import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {

  implicit val PartitionStatusFormat = jsonFormat4(PartitionStatus)
  implicit val MachineStatusFormat = jsonFormat6(MachineStatus)
}
