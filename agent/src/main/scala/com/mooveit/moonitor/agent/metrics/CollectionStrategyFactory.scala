package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

trait CollectionStrategyFactory {

  def getCollectionStrategy(metric: Metric): CollectionStrategy
}

object CollectionStrategyFactory extends CollectionStrategyFactory{

  override def getCollectionStrategy(metric: Metric) = metric match {
    case _: KernelMetric =>
      KernelMetricCollectionStrategyFactory.getCollectionStrategy(metric)

    case _: NetworkMetric =>
      NetworkMetricCollectionStrategyFactory.getCollectionStrategy(metric)

    case _: ProcessesMetric =>
      ProcessesMetricCollectionStrategy.getCollectionStrategy(metric)

    case _: SystemMetric =>
      SystemMetricCollectionStrategyFactory.getCollectionStrategy(metric)
  }
}
