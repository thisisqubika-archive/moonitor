package com.mooveit.moonitor.collector.strategies

class MaxFilesStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getResourceLimit.getOpenFilesMax
}

class MaxProcessesStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getResourceLimit.getProcessesMax
}
