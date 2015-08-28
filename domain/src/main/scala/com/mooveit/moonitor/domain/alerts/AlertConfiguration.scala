package com.mooveit.moonitor.domain.alerts

import com.mooveit.moonitor.domain.metrics.MetricId

case class AlertConfiguration(metricId: MetricId,
                              operator: Operator,
                              value: Any,
                              mailTo: String)
