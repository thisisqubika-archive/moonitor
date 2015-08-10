package com.mooveit.moonitor.domain.metrics

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id

@JsonTypeInfo(use = Id.NAME)
@JsonSubTypes(Array(new Type(value = classOf[CpuLoad], name = "CpuLoad")))
trait Metric
