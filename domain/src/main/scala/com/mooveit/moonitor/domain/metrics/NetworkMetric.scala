package com.mooveit.moonitor.domain.metrics

trait NetworkMetric extends Metric

case class UdpListen(port: Int) extends NetworkMetric

case class InterfaceIn(name: String) extends NetworkMetric

case class InterfaceOut(name: String) extends NetworkMetric
