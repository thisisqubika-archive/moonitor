package com.mooveit.moonitor.collector.strategies

case class UdpListenStrategy(port: Int) extends HostMetricCollectionStrategy {

  override def collectValue = throw new UnsupportedOperationException
}

case class InterfaceInStrategy(name: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getNetInterfaceStat(name).getRxPackets
}

case class InterfaceOutStrategy(name: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getNetInterfaceStat(name).getTxPackets
}
