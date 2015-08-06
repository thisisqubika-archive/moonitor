package com.mooveit.moonitor.domain.metrics

trait SystemMetric extends Metric

case object HostBootTime extends SystemMetric

case object InterruptsPerSecond extends SystemMetric

case class CpuLoad(cpu: String, mode: String) extends SystemMetric

case object CpuSwitches extends SystemMetric

case class CpuUtilization(cpu: String,
                          utilType: String,
                          mode: String) extends SystemMetric
