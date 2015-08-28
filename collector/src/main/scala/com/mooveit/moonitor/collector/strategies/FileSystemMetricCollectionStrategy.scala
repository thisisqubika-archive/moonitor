package com.mooveit.moonitor.collector.strategies

case class FreeSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getFileSystemUsage(dir).getFree
}

case class TotalSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getFileSystemUsage(dir).getTotal
}

case class UsedSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getFileSystemUsage(dir).getUsed
}
