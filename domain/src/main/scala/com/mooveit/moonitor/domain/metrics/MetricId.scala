package com.mooveit.moonitor.domain.metrics

case class MetricId(className: String, params: String) {

  override def toString = s"$className($params)"
}
