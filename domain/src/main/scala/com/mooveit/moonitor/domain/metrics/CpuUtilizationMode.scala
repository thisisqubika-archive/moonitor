package com.mooveit.moonitor.domain.metrics

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

@JsonTypeInfo(use = Id.CLASS)
sealed trait CpuUtilizationMode

case object Idle extends CpuUtilizationMode

case object Interrupt extends CpuUtilizationMode

case object IOWait extends CpuUtilizationMode

case object Nice extends CpuUtilizationMode

case object SoftIrq extends CpuUtilizationMode

case object Steal extends CpuUtilizationMode

case object System extends CpuUtilizationMode

case object User extends CpuUtilizationMode
