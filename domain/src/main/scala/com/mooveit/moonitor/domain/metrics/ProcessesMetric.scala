package com.mooveit.moonitor.domain.metrics

trait ProcessesMetric extends Metric

case class NumberOfProcesses(procId: String,
                             user: String,
                             status: String) extends ProcessesMetric
