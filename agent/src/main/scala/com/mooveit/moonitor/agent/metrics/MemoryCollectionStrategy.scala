package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

object FreeMemoryStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getMem.getActualFree
}

object TotalMemoryStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getMem.getTotal
}

object UsedMemoryStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getMem.getActualUsed
}

object MemoryCollectionStrategyFactory extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case FreeMemory => FreeMemoryStrategy

    case TotalMemory => TotalMemoryStrategy

    case UsedMemory => UsedMemoryStrategy
  }
}
