package com.mooveit.moonitor.domain.metrics

trait NetworkMetric extends Metric

case class UdpListen(port: Int) extends NetworkMetric
