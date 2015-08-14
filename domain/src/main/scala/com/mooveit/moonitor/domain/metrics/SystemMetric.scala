package com.mooveit.moonitor.domain.metrics

trait SystemMetric extends Metric

case object HostBootTime extends SystemMetric

case object InterruptsPerSecond extends SystemMetric

case object CpuLoad extends SystemMetric

case object CpuSwitches extends SystemMetric

case class CpuUtilization(mode: CpuUtilizationMode) extends SystemMetric

case object HostName extends SystemMetric

case object HostLocalTime extends SystemMetric

case object SystemName extends SystemMetric

case object SystemUptime extends SystemMetric

case object LoggedUsers extends SystemMetric
