package com.mooveit.moonitor.principal

import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.domain.alerts.{Gt, AlertConfiguration}
import com.mooveit.moonitor.domain.metrics.{MetricId, MetricConfiguration}
import com.mooveit.moonitor.principal.actors.ConfigurationService
import com.mooveit.moonitor.principal.actors.Mastermind._

import com.mooveit.moonitor.principal.serialization.JacksonJsonSupport._

class TestConfigurationService extends RouteTestSpec {

  val mastermind = TestProbe()
  val configurationService =
    TestActorRef[ConfigurationService](
      ConfigurationService.props(mastermind.ref))
  val route = configurationService.underlyingActor.crudService

  val testClassName = "com.mooveit.moonitor.principal.TestMetricStrategy"
  val testMetricId = MetricId(testClassName, "1")
  val mconf = MetricConfiguration(testMetricId, 1000)
  val aconf = AlertConfiguration(testMetricId, Gt, 1000, "")

  "The service" when {
    "post host" should {
      "notify mastermind" in {
        Post("/hosts/127.0.0.1") ~> route ~> check {
          mastermind.expectMsg(StartHost("127.0.0.1"))
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }

    "delete host" should {
      "notify mastermind" in {
        Delete("/hosts/127.0.0.1") ~> route ~> check {
          mastermind.expectMsg(StopHost("127.0.0.1"))
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }

    "put metric" should {
      "notify mastermind" in {
        Put("/hosts/127.0.0.1/metrics", mconf) ~> route ~> check {
          mastermind.expectMsg(StartCollecting("127.0.0.1", mconf))
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }

    "delete metric" should {
      "notify mastermind" in {
        Delete("/hosts/127.0.0.1/metrics", testMetricId) ~> route ~> check {
          mastermind.expectMsg(StopCollecting("127.0.0.1", testMetricId))
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }

    "put alert" should {
      "notify mastermind" in {
        Put("/hosts/127.0.0.1/alerts", aconf) ~> route ~> check {
          mastermind.expectMsgClass(classOf[StartWatching])
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }

    "delete alert" should {
      "notify mastermind" in {
        Delete("/hosts/127.0.0.1/alerts", testMetricId) ~> route ~> check {
          mastermind.expectMsg(StopWatching("127.0.0.1", testMetricId))
          status.intValue shouldEqual 200
          responseAs[String] shouldEqual "Ok"
        }
      }
    }
  }
}
