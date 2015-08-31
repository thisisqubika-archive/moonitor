package com.mooveit.moonitor.principal

import akka.testkit.{TestProbe, TestActorRef}
import com.mooveit.moonitor.agent.actors.Agent.MetricCollected
import com.mooveit.moonitor.domain.alerts.AlertConfiguration
import com.mooveit.moonitor.domain.metrics.{MetricResult, MetricId}
import com.mooveit.moonitor.principal.actors.MailInformer.{AlertCleared, Alert}
import com.mooveit.moonitor.principal.actors.Watcher
import com.mooveit.moonitor.principal.actors.Watcher._
import com.mooveit.moonitor.domain.alerts.Gt

import scala.concurrent.duration._

class TestWatcher extends UnitSpec("Watcher") {

  val testClassName = "com.mooveit.moonitor.agent.TestMetricStrategy"
  val testMetricId1 = MetricId(testClassName, "1")
  val testMetricId2 = MetricId(testClassName, "2")

  val informer = TestProbe()
  val watcher = TestActorRef(Watcher.props("localhost",
    Seq(AlertConfiguration(testMetricId2, Gt, 1, "")), informer.ref))

  "A watcher" when {
    "start watching received" should {
      "add configuration" in {
        watcher ! StartWatching(AlertConfiguration(testMetricId1, Gt, 1, ""))
        watcher ! StartWatching(AlertConfiguration(testMetricId2, Gt, 1, ""))
        watcher ! GetConfiguration
        val configuration =
          receiveOne(1.second).asInstanceOf[Map[MetricId, AlertConfiguration]]
        configuration should have size 2
        configuration.contains(testMetricId1) shouldBe true
        configuration.contains(testMetricId2) shouldBe true
      }
    }

    "stop watching received" should {
      "remove configuration" in {
        watcher ! StopWatching(testMetricId2)
        watcher ! GetConfiguration
        val configuration =
          receiveOne(1.second).asInstanceOf[Map[MetricId, AlertConfiguration]]
        configuration should have size 1
      }
    }

    "metric collected" when {
      "metric configured" when {
        "threshold trespased" when {
          "not alerted before" should {
            "notify mailer" in {
              watcher ! MetricCollected(testMetricId1, MetricResult(1234, 3))
              informer.expectMsgClass(classOf[Alert])
            }
          }

          "alerted before" should {
            "do nothing" in {
              watcher ! MetricCollected(testMetricId1, MetricResult(1234, 4))
              informer.expectNoMsg()
            }
          }
        }

        "threshold not trepassed" when {
          "alerted before" should {
            "clear alert" in {
              watcher ! MetricCollected(testMetricId1, MetricResult(1234, 0))
              informer.expectMsgClass(classOf[AlertCleared])
            }
          }

          "not alerted before" should {
            "do nothing" in {
              watcher ! MetricCollected(testMetricId1, MetricResult(1234, 0))
              informer.expectNoMsg()
            }
          }
        }
      }

      "metric not configured" should {
        "do nothing" in {
          watcher ! MetricCollected(testMetricId2, MetricResult(1234, 2))
          informer.expectNoMsg()
        }
      }
    }
  }
}
