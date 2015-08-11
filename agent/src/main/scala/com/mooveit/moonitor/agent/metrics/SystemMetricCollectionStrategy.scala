package com.mooveit.moonitor.agent.metrics

import com.mooveit.moonitor.domain.metrics._

import scala.util.Random

object HostBootTimeStrategy extends CollectionStrategy {

  override def collect = Random.nextInt()
}

object InterruptsPerSecondStrategy extends CollectionStrategy {

  override def collect = Random.nextInt()
}

case class CpuLoadStrategy(cpu: String, mode: String)
  extends CollectionStrategy {

  override def collect = Random.nextInt()
}

object CpuSwitchesStrategy extends CollectionStrategy {

  override def collect = Random.nextInt()
}

case class CpuUtilizationStrategy(cpu: String,
                                  utilType: String,
                                  mode: String) extends CollectionStrategy {

  override def collect = Random.nextInt()
}

object SystemMetricCollectionStrategyFactory extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case HostBootTime => HostBootTimeStrategy

    case InterruptsPerSecond => InterruptsPerSecondStrategy

    case CpuLoad(cpu, mode) => CpuLoadStrategy(cpu, mode)

    case CpuSwitches => CpuSwitchesStrategy

    case CpuUtilization(cpu, utilType, mode) =>
      CpuUtilizationStrategy(cpu, utilType, mode)
  }
}
