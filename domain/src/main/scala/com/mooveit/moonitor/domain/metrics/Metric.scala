package com.mooveit.moonitor.domain.metrics

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

@JsonTypeInfo(use = Id.CLASS)
trait Metric {

  def id = getClass.getSimpleName
}
