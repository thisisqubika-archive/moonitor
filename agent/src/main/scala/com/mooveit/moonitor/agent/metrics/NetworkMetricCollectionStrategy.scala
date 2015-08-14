package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

case class UdpListenStrategy(port: Int) extends HostMetricCollectionStrategy {

  override def collect = throw new UnsupportedOperationException
}

case class InterfaceInStrategy(name: String)
  extends HostMetricCollectionStrategy {

  override def collect = sigar.getNetInterfaceStat(name).getRxPackets
}

case class InterfaceOutStrategy(name: String)
  extends HostMetricCollectionStrategy {

  override def collect = sigar.getNetInterfaceStat(name).getTxPackets
}

object NetworkMetricCollectionStrategyFactory
  extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case UdpListen(port) => UdpListenStrategy(port)

    case InterfaceIn(name) => InterfaceInStrategy(name)

    case InterfaceOut(name) => InterfaceOutStrategy(name)
  }
}
