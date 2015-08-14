package com.mooveit.moonitor.agent.metrics

import org.hyperic.sigar.{Humidor, Sigar}

trait CollectionStrategy {

  def collect: Any
}

trait HostMetricCollectionStrategy extends CollectionStrategy {

  val humidor = new Humidor(new Sigar)

  def sigar = humidor.getSigar
}
