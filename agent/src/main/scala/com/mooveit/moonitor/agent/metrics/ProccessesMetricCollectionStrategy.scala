package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._
import org.hyperic.sigar.SigarException

case class NumberOfProcessesStrategy(status: Char)
  extends HostMetricCollectionStrategy {

  override def collect =
    sigar.getProcList.map(pid =>
      sigar.getProcState(pid).getState).count(_ == status)
}

case class ProcessStatusStrategy(ptql: String)
  extends HostMetricCollectionStrategy {

  override def collect = try {
    sigar.getProcState(ptql)
    1
  } catch {
    case _: SigarException => 0
  }
}

object ProcessesMetricCollectionStrategy extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case NumberOfProcesses(status) => NumberOfProcessesStrategy(status)

    case ProcessStatus(ptql) => ProcessStatusStrategy(ptql)
  }
}
