package com.mooveit.moonitor.domain.metrics

import scala.concurrent.duration.FiniteDuration

case class MetricConfiguration(metric: Metric, frequency: FiniteDuration)
