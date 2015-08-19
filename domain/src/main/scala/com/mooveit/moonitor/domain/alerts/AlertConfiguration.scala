package com.mooveit.moonitor.domain.alerts

import com.mooveit.moonitor.domain.metrics.Metric

case class AlertConfiguration(metric: Metric,
                              operator: Operator,
                              value: Any,
                              mailTo: String)
