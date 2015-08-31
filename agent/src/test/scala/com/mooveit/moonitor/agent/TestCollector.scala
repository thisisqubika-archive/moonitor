package com.mooveit.moonitor.agent

import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.agent.actors.Collector
import com.mooveit.moonitor.agent.actors.Collector._
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricId}

import scala.concurrent.duration._

class TestCollector extends UnitSpec("Collector") {

  val testClassName = "com.mooveit.moonitor.agent.TestMetricStrategy"
  val testMetricId = MetricId(testClassName, "param")
  val mconf = MetricConfiguration(testMetricId, 1000)

  val agent = TestProbe()
  val collector = TestActorRef(Collector.props(mconf), agent.ref, "collector")

  "A collector" when {
    "created" should {
      "set up frequency" in {
        collector ! GetFrequency
        expectMsg(1000)
      }
    }

    "frequency changed" should {
      "update frequency" in {
        collector ! ChangeFrequency(800)
        collector ! GetFrequency
        expectMsg(800)
      }
    }

    "frequency" should {
      "notify agent" in {
        agent.expectMsgClass(500.millis, classOf[MetricCollected])
      }
    }
  }
}
