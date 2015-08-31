package com.mooveit.moonitor.principal

import com.mooveit.moonitor.domain.metrics.CollectionStrategy

case class TestMetricStrategy(someParam: String) extends CollectionStrategy {

  override def collectValue = someParam.toInt
}
