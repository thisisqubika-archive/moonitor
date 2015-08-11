package com.mooveit.moonitor.agent.metrics

trait CollectionStrategy {

  def collect: Any
}
