package com.mooveit.moonitor.collector.strategies

import scala.util.{Failure, Success, Try}

class NumberOfProcessesStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getProcList.size
}

case class ProcessStatusStrategy(ptql: String)
  extends HostMetricCollectionStrategy {

  override def collectValue =
    Try(sigar.getProcState(ptql)) match {
      case Success(_) => 1
      case Failure(_) => 0
    }
}
