package com.mooveit.moonitor.collector.strategies

class FreeSwapStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getSwap.getFree
}

class TotalSwapStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getSwap.getTotal
}

class FreeSwapPercStrategy extends HostMetricCollectionStrategy {

  override def collectValue =
    (sigar.getSwap.getFree / sigar.getSwap.getTotal) * 100
}
