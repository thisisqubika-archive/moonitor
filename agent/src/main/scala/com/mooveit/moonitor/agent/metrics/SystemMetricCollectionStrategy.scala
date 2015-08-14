package com.mooveit.moonitor.agent.metrics

import java.net.InetAddress

import com.mooveit.moonitor.domain.metrics._

import scala.compat.Platform.currentTime

object HostBootTimeStrategy extends HostMetricCollectionStrategy {

  override def collect = throw new UnsupportedOperationException
}

object InterruptsPerSecondStrategy extends HostMetricCollectionStrategy {

  override def collect = throw new UnsupportedOperationException
}

object CpuLoadStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getCpuPerc.getCombined
}

object CpuSwitchesStrategy extends HostMetricCollectionStrategy {

  override def collect = throw new UnsupportedOperationException
}

case class CpuUtilizationStrategy(mode: CpuUtilizationMode)
  extends HostMetricCollectionStrategy {

  override def collect = mode match {
    case Idle => sigar.getCpuPerc.getIdle

    case Interrupt => sigar.getCpuPerc.getIrq

    case IOWait => sigar.getCpuPerc.getWait

    case Nice => sigar.getCpuPerc.getNice

    case SoftIrq => sigar.getCpuPerc.getSoftIrq

    case Steal => sigar.getCpuPerc.getStolen

    case System => sigar.getCpuPerc.getSys

    case User => sigar.getCpuPerc.getUser
  }
}

object HostNameStrategy extends CollectionStrategy {

  override def collect = InetAddress.getLocalHost.getHostName
}

object HostLocalTimeStrategy extends CollectionStrategy {

  override def collect = currentTime
}

object SystemNameStrategy extends CollectionStrategy {

  override def collect = java.lang.System.getProperty("os.name")
}

object SystemUptimeStrategy extends HostMetricCollectionStrategy {

  override def collect = sigar.getUptime.getUptime
}

object LoggedUsersStrategy extends HostMetricCollectionStrategy {

  override def collect = throw new UnsupportedOperationException
}

object SystemMetricCollectionStrategyFactory extends CollectionStrategyFactory {

  override def getCollectionStrategy(metric: Metric) = metric match {
    case HostBootTime => HostBootTimeStrategy

    case InterruptsPerSecond => InterruptsPerSecondStrategy

    case CpuLoad => CpuLoadStrategy

    case CpuSwitches => CpuSwitchesStrategy

    case CpuUtilization(mode) => CpuUtilizationStrategy(mode)

    case HostName => HostNameStrategy

    case HostLocalTime => HostLocalTimeStrategy

    case SystemName => SystemNameStrategy

    case SystemUptime => SystemUptimeStrategy

    case LoggedUsers => LoggedUsersStrategy
  }
}
