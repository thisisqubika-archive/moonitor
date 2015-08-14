package com.mooveit.moonitor.domain.metrics

trait SwapMetric extends Metric

case object FreeSwap extends SwapMetric

case object TotalSwap extends SwapMetric

case object FreeSwapPerc extends SwapMetric
