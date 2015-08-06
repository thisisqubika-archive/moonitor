package com.mooveit.moonitor.domain.metrics

trait KernelMetric extends Metric

case object MaxFiles extends KernelMetric

case object MaxProcesses extends KernelMetric
