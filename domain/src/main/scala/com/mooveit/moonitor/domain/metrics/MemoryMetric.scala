package com.mooveit.moonitor.domain.metrics

trait MemoryMetric extends Metric

case object FreeMemory extends MemoryMetric

case object TotalMemory extends MemoryMetric

case object UsedMemory extends MemoryMetric
