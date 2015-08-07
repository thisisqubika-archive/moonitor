package com.mooveit.moonitor.domain.metrics

case class MetricValue(metric: Metric, timestamp: Long, value: Any)
