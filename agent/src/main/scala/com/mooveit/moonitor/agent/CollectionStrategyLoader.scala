package com.mooveit.moonitor.agent

import java.net.{URL, URLClassLoader}

import com.mooveit.moonitor.domain.metrics.{CollectionStrategy, MetricId}

import scala.util.{Failure, Success, Try}

object CollectionStrategyLoader {

  val config = Main.config
  
  def loadCollectionStrategy(metricId: MetricId, packageName: String = "") = {
    val parentLoader = getClass.getClassLoader

    val loader =
      if (packageName.isEmpty) parentLoader
      else {
        val path = s"file:/${config.getString("plugins.dir")}/$packageName"
        new URLClassLoader(Array(new URL(path)), parentLoader)
      }

    val className = s"${metricId.packageName}.${metricId.className}"
    val classToLoad = Class.forName(className, true, loader)
    val instance =
      Try(classToLoad.getConstructor(classOf[String])) match {
        case Success(c) => c.newInstance(metricId.params)

        case Failure(_: NoSuchMethodException) =>
          classToLoad.getConstructor().newInstance()

        case Failure(ex) => throw ex
      }

    instance.asInstanceOf[CollectionStrategy]
  }
}
