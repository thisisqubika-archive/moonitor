package com.mooveit.moonitor.domain.metrics

trait ProcessesMetric extends Metric

case class NumberOfProcesses(status: Char) extends ProcessesMetric

case class ProcessStatus(ptql: String) extends ProcessesMetric
