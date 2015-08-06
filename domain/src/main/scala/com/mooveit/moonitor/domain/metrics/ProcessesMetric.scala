package com.mooveit.moonitor.domain.metrics

trait ProcessesMetric extends Metric

case class NumberOfProcesses(name: String,
                             user: String,
                             status: String) extends ProcessesMetric
