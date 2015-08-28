package com.mooveit.moonitor.domain.metrics

case class MetricConfiguration(packageName: String,
                               metricId: MetricId,
                               frequency: Int)
