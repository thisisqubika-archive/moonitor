package com.mooveit.moonitor.collector.strategies

import scala.util.{Failure, Success, Try}

case class NumberOfProcessesStrategy(status: Char)
  extends HostMetricCollectionStrategy {

  override def collectValue =
    sigar.getProcList.map(pid =>
      sigar.getProcState(pid).getState).count(_ == status)
}

case class ProcessStatusStrategy(ptql: String)
  extends HostMetricCollectionStrategy {

  override def collectValue =
    Try(sigar.getProcState(ptql)) match {
      case Success(_) => 1
      case Failure(_) => 0
    }
}
