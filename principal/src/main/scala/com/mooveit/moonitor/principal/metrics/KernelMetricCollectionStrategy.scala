package com.mooveit.moonitor.principal.metrics

import com.mooveit.moonitor.domain.metrics.{MaxFiles, MaxProcesses, Metric}

import scala.util.Random

object MaxFilesStrategy extends CollectionStrategy {

  override def collect = Random.nextInt()
}

object MaxProcessesStrategy extends CollectionStrategy {

  override def collect = Random.nextInt()
}

object KernelMetricCollectionStrategyFactory {

  def getCollectionStrategy(metric: Metric) = metric match {
    case MaxFiles => MaxFilesStrategy

    case MaxProcesses => MaxProcessesStrategy
  }
}
