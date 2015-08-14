package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics.{MaxFiles, MaxProcesses, Metric}

object MaxFilesStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getResourceLimit.getOpenFilesMax
}

object MaxProcessesStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getResourceLimit.getProcessesMax
}

object KernelMetricCollectionStrategyFactory {

  def getCollectionStrategy(metric: Metric) = metric match {
    case MaxFiles => MaxFilesStrategy

    case MaxProcesses => MaxProcessesStrategy
  }
}
