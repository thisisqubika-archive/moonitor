package com.mooveit.moonitor.domain.metrics

case class MetricId(packageName: String,
                    className: String,
                    metricName: String,
                    params: String) {

  override def toString = {
    val paramsString = if (params.nonEmpty) s"($params)" else ""
    s"$metricName$paramsString"
  }
}
