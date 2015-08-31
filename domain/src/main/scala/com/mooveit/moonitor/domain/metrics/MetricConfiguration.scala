package com.mooveit.moonitor.domain.metrics

case class MetricConfiguration(packageName: String,
                               metricId: MetricId,
                               frequency: Int)

object MetricConfiguration {

  def apply(metricId: MetricId, frequency: Int): MetricConfiguration =
    apply("", metricId, frequency)
}
