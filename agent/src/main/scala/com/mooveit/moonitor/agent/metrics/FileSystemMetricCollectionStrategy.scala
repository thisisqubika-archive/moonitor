package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

case class FreeSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collect = sigar.getFileSystemUsage(dir).getFree
}

case class TotalSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collect = sigar.getFileSystemUsage(dir).getTotal
}

case class UsedSpaceStrategy(dir: String)
  extends HostMetricCollectionStrategy {

  override def collect = sigar.getFileSystemUsage(dir).getUsed
}

object FileSystemMetricCollectionStrategyFactory
  extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case FreeSpace(dir) => FreeSpaceStrategy(dir)

    case TotalSpace(dir) => TotalSpaceStrategy(dir)

    case UsedSpace(dir) => UsedSpaceStrategy(dir)
  }
}
