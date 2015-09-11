package com.mooveit.moonitor.principal

import akka.actor.ActorRef
import akka.testkit.TestActor.AutoPilot
import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.domain.alerts.{AlertConfiguration, Gt}
import com.mooveit.moonitor.domain.metrics.{MetricConfiguration, MetricId}
import com.mooveit.moonitor.principal.actors.ConfigurationStore._
import com.mooveit.moonitor.principal.actors.Mastermind
import com.mooveit.moonitor.principal.actors.Mastermind._
import com.mooveit.moonitor.principal.actors.Principal._

import scala.concurrent.duration._

class TestMastermind extends UnitSpec("Mastermind") {

  val testPackageName = "com.mooveit.moonitor.principal"
  val testClassName = "TestMetricStrategy"
  val testMetricId = MetricId(testPackageName, testClassName, "1")
  val metricConfiguration = MetricConfiguration(testMetricId, 1000)
  val alertConfiguration = AlertConfiguration(testMetricId, Gt, 1000, "")

  val store = TestProbe()
  val confStore = TestProbe()
  val mastermind = TestActorRef(Mastermind.props(store.ref, confStore.ref))

  confStore.setAutoPilot(new AutoPilot {
    override def run(sender: ActorRef, msg: Any) = {
      msg match {
        case RetrieveMetricsConfig(_) =>
          sender ! MetricsConfiguration(metricConfiguration)

        case RetrieveAlertsConfig(_) =>
          sender ! AlertsConfiguration(alertConfiguration)

        case _ =>
      }

      keepRunning
    }
  })


  "A mastermind" when {
    "starts up" should {
      "ask for configuration" in {
        confStore.expectMsg(RetrieveConfiguredHosts)
      }
    }

    "receives configuration" should {
      "create principal" in {
        mastermind ! ConfiguredHosts("127.0.0.1")
        mastermind ! GetPrincipals
        val principals =
          receiveOne(1.second).asInstanceOf[Map[String, ActorRef]]

        principals should have size 1
        principals.contains("127.0.0.1") shouldBe true
        confStore.expectMsg(RetrieveMetricsConfig("127.0.0.1"))
        confStore.expectMsg(RetrieveAlertsConfig("127.0.0.1"))
      }
    }

    "receives start host" when {
      "host already exists" should {
        "do nothing" in {
          mastermind ! StartHost("127.0.0.1")
          confStore.expectNoMsg()
        }
      }

      "host doesn't exist" should {
        "add host" in {
          mastermind ! StartHost("10.100.19.81")
          mastermind ! GetPrincipals
          val principals =
            receiveOne(1.second).asInstanceOf[Map[String, ActorRef]]

          principals should have size 2
          principals.contains("10.100.19.81") shouldBe true
          confStore.expectMsg(RetrieveMetricsConfig("10.100.19.81"))
          confStore.expectMsg(RetrieveAlertsConfig("10.100.19.81"))
          confStore.expectMsg(SaveHost("10.100.19.81"))
        }
      }
    }

    "receives stop host" when {
      "host already exists" should {
        "remove host" in {
          mastermind ! StopHost("127.0.0.1")
          mastermind ! GetPrincipals
          val principals =
            receiveOne(1.second).asInstanceOf[Map[String, ActorRef]]

          principals should have size 1
          principals.contains("10.100.19.81") shouldBe true
          principals.contains("127.0.0.1") shouldBe false
          confStore.expectMsg(RemoveHost("127.0.0.1"))
        }
      }

      "host doesn't exist" should {
        "do nothing" in {
          mastermind ! StopHost("192.168.1.1")
          confStore.expectNoMsg()
        }
      }
    }

    "receives start collecting" when {
      "host already exists" should {
        "notify principal" in {
          mastermind ! StartCollecting("10.100.19.81", metricConfiguration)
          confStore.expectMsg(SaveMetric("10.100.19.81", metricConfiguration))
        }
      }

      "host doesn't exist" should {
        "do nothing" in {
          mastermind ! StartCollecting("10.10.10.10", metricConfiguration)
          confStore.expectNoMsg()
        }
      }
    }

    "receives stop collecting" when {
      "host already exists" should {
        "notify principal" in {
          mastermind ! StopCollecting("10.100.19.81", testMetricId)
          confStore.expectMsg(RemoveMetric("10.100.19.81", testMetricId))
        }
      }

      "host doesn't exist" should {
        "do nothing" in {
          mastermind ! StopCollecting("192.168.1.1", testMetricId)
          confStore.expectNoMsg()
        }
      }
    }

    "receives start watching" when {
      "host already exists" should {
        "notify principal" in {
          mastermind ! StartWatching("10.100.19.81", alertConfiguration)
          confStore.expectMsg(SaveAlert("10.100.19.81", alertConfiguration))
        }
      }

      "host doesn't exist" should {
        "do nothing" in {
          mastermind ! StartWatching("10.10.10.10", alertConfiguration)
          confStore.expectNoMsg()
        }
      }
    }

    "receives stop watching" when {
      "host already exists" should {
        "notify principal" in {
          mastermind ! StopWatching("10.100.19.81", testMetricId)
          confStore.expectMsg(RemoveAlert("10.100.19.81", testMetricId))
        }
      }

      "host doesn't exist" should {
        "do nothing" in {
          mastermind ! StopWatching("192.168.1.1", testMetricId)
          confStore.expectNoMsg()
        }
      }
    }
  }
}
