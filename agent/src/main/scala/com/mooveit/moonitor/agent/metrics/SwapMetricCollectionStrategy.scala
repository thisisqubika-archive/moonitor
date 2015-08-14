package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

object FreeSwapStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getSwap.getFree
}

object TotalSwapStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getSwap.getTotal
}

object FreeSwapPercStrategy extends HostMetricCollectionStrategy {

  override def collect = (sigar.getSwap.getFree / sigar.getSwap.getTotal) * 100
}

object SwapMetricCollectionStrategyFactory extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case FreeSwap => FreeSwapStrategy

    case TotalSwap => TotalSwapStrategy

    case FreeSwapPerc => FreeSwapPercStrategy
  }
}
