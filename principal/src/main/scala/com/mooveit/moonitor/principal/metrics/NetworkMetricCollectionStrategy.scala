package com.mooveit.moonitor.principal.metrics

import com.mooveit.moonitor.domain.metrics.{UdpListen, Metric}

import scala.util.Random

case class UdpListenStrategy(port: Int) extends CollectionStrategy {

  override def collect = Random.nextInt(port)
}

object NetworkMetricCollectionStrategyFactory
  extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case UdpListen(port) => UdpListenStrategy(port)
  }
}
