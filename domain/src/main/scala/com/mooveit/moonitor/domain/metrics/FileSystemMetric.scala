package com.mooveit.moonitor.domain.metrics

trait FileSystemMetric extends Metric

case class FreeSpace(dir: String) extends FileSystemMetric

case class TotalSpace(dir: String) extends FileSystemMetric

case class UsedSpace(dir: String) extends FileSystemMetric
