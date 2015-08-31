package com.mooveit.moonitor.principal

import akka.actor.ActorRef
import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.domain.alerts.{AlertConfiguration, Gt}
import com.mooveit.moonitor.domain.metrics.{MetricResult, MetricConfiguration, MetricId}
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.mooveit.moonitor.principal.actors.Principal
import com.mooveit.moonitor.principal.actors.Principal._
import com.mooveit.moonitor.principal.actors.Watcher.{StopWatching, GetConfiguration, StartWatching}

import scala.concurrent.duration._

class TestPrincipal extends UnitSpec("Principal") {

  val store = TestProbe()
  val confStore = TestProbe()
  val principal =
    TestActorRef(Principal.props("localhost", store.ref, confStore.ref))

  val testClassName = "com.mooveit.moonitor.principal.TestMetricStrategy"
  val testMetricId = MetricId(testClassName, "1")
  val testConfiguration = MetricConfiguration(testMetricId, 1000)

  "A principal" when {
    "created" should {
      "ask for configuration" in {
        confStore.expectMsgClass(classOf[RetrieveMetricsConfig])
        confStore.expectMsgClass(classOf[RetrieveAlertsConfig])
      }
    }

    "metric configuration received" should {
      "create agent" in {
        principal.children should have size 1 // The mail informer
        principal ! MetricsConfiguration(testConfiguration)
        principal.children should have size 2
      }
    }

    "alert configuration received" should {
      "create watcher" in {
        principal.children should have size 2
        val configuration = AlertConfiguration(testMetricId, Gt, 1, "")
        principal ! AlertsConfiguration(configuration)
        principal.children should have size 3
      }
    }

    "metric collected" should {
      "save into store" in {
        val result = MetricResult(1234567, 1)
        principal ! MetricCollected(testMetricId, result)

        store.expectMsg(Save("localhost", testMetricId, result))
      }
    }

    "start collecting received" should {
      "save configuration" in {
        principal ! StartCollecting(testConfiguration)
        confStore.expectMsgClass(classOf[SaveMetric])
      }

      "notify agent" in {
        principal ! StartCollecting(testConfiguration)
        principal.getSingleChild("agent-localhost") ! GetCollectors
        val collectors =
          receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

        confStore.expectMsgClass(classOf[SaveMetric])
        collectors.contains(testMetricId) shouldBe true
      }
    }

    "stop collecting received" should {
      "remove configuration" in {
        principal ! StopCollecting(testMetricId)
        confStore.expectMsgClass(classOf[RemoveMetric])
      }

      "notify agent" in {
        principal ! StopCollecting(testMetricId)
        principal.getSingleChild("agent-localhost") ! GetCollectors
        val collectors =
          receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

        confStore.expectMsgClass(classOf[RemoveMetric])
        collectors.contains(testMetricId) shouldBe false
      }
    }

    "start watching received" should {
      "save configuration" in {
        principal ! StartWatching(AlertConfiguration(testMetricId, Gt, 9, ""))
        confStore.expectMsgClass(classOf[SaveAlert])
      }

      "notify watcher" in {
        principal ! StartWatching(AlertConfiguration(testMetricId, Gt, 9, ""))
        principal.getSingleChild("watcher-localhost") ! GetConfiguration
        val configuration =
          receiveOne(1.second).asInstanceOf[Map[MetricId, AlertConfiguration]]

        confStore.expectMsgClass(classOf[SaveAlert])
        configuration.contains(testMetricId) shouldBe true
      }
    }

    "stop watching received" should {
      "remove configuration" in {
        principal ! StopWatching(testMetricId)
        confStore.expectMsgClass(classOf[RemoveAlert])
      }

      "notify watcher" in {
        principal ! StopWatching(testMetricId)
        principal.getSingleChild("watcher-localhost") ! GetConfiguration
        val configuration =
          receiveOne(1.second).asInstanceOf[Map[MetricId, AlertConfiguration]]

        confStore.expectMsgClass(classOf[RemoveAlert])
        configuration.contains(testMetricId) shouldBe false
      }
    }

    "stop received" should {
      "stop others" in {
        principal ! Stop
        principal.children shouldBe empty
      }
    }
  }
}
