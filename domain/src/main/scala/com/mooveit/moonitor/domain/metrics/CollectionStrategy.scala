package com.mooveit.moonitor.domain.metrics

import scala.compat.Platform.currentTime

trait CollectionStrategy {

  final def collect: MetricResult = {
    val result = collectValue
    MetricResult(currentTime, result)
  }

  def collectValue: Any
}
