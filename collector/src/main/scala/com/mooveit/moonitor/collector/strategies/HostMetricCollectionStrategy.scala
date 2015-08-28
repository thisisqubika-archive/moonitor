package com.mooveit.moonitor.collector.strategies

import com.mooveit.moonitor.domain.metrics.CollectionStrategy
import org.hyperic.sigar.{Humidor, Sigar}

trait HostMetricCollectionStrategy extends CollectionStrategy {

  val humidor = new Humidor(new Sigar)

  def sigar = humidor.getSigar
}
