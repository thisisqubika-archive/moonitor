package com.mooveit.moonitor.agent

import com.mooveit.moonitor.domain.metrics.CollectionStrategy

case class TestMetricStrategy(someParam: String) extends CollectionStrategy {

  override def collectValue = s"some-value-$someParam"
}
