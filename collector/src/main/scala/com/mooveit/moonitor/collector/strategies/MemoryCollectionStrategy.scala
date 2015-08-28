package com.mooveit.moonitor.collector.strategies

class FreeMemoryStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getMem.getActualFree
}

class TotalMemoryStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getMem.getTotal
}

class UsedMemoryStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getMem.getActualUsed
}
