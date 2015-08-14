package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics.{Metric, NumberOfProcesses}

case class NumberOfProcessesStrategy(status: Char)
  extends HostMetricCollectionStrategy {

  override def collect =
    sigar.getProcList.map(pid => sigar.getProcState(pid).getState).count(_ == status)
}

object ProcessesMetricCollectionStrategy extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case NumberOfProcesses(status) => NumberOfProcessesStrategy(status)
  }
}
