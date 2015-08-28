package com.mooveit.moonitor.collector.strategies

import java.net.InetAddress

import com.mooveit.moonitor.domain.metrics.CollectionStrategy

import scala.compat.Platform.currentTime

class HostBootTimeStrategy extends HostMetricCollectionStrategy {

  override def collectValue = throw new UnsupportedOperationException
}

class InterruptsPerSecondStrategy extends HostMetricCollectionStrategy {

  override def collectValue = throw new UnsupportedOperationException
}

class CpuLoadStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getCpuPerc.getCombined
}

class CpuSwitchesStrategy extends HostMetricCollectionStrategy {

  override def collectValue = throw new UnsupportedOperationException
}

case class CpuUtilizationStrategy(mode: String)
  extends HostMetricCollectionStrategy {

  override def collectValue = mode match {
    case "Idle" => sigar.getCpuPerc.getIdle

    case "Interrupt" => sigar.getCpuPerc.getIrq

    case "IOWait" => sigar.getCpuPerc.getWait

    case "Nice" => sigar.getCpuPerc.getNice

    case "SoftIrq" => sigar.getCpuPerc.getSoftIrq

    case "Steal" => sigar.getCpuPerc.getStolen

    case "System" => sigar.getCpuPerc.getSys

    case "User" => sigar.getCpuPerc.getUser
  }
}

class HostNameStrategy extends CollectionStrategy {

  override def collectValue = InetAddress.getLocalHost.getHostName
}

class HostLocalTimeStrategy extends CollectionStrategy {

  override def collectValue = currentTime
}

class SystemNameStrategy extends CollectionStrategy {

  override def collectValue = java.lang.System.getProperty("os.name")
}

class SystemUptimeStrategy extends HostMetricCollectionStrategy {

  override def collectValue = sigar.getUptime.getUptime
}

class LoggedUsersStrategy extends HostMetricCollectionStrategy {

  override def collectValue = throw new UnsupportedOperationException
}
