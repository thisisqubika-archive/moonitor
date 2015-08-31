package com.mooveit.moonitor.agent

import akka.actor.ActorRef
import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.agent.actors.Agent
import com.mooveit.moonitor.agent.actors.Agent._
import com.mooveit.moonitor.agent.actors.Collector.GetFrequency
import com.mooveit.moonitor.domain.metrics._

import scala.concurrent.duration._

class TestAgent extends UnitSpec("Agent") {

  val testClassName = "com.mooveit.moonitor.agent.TestMetricStrategy"
  val testMetricId = MetricId(testClassName, "param")

  "An agent" when {
    val principal = TestProbe()
    val agent =
      TestActorRef(Agent.props(Seq()), principal.ref, "agent-no-conf")

    "metric collected" should {
      val msg = MetricCollected(testMetricId, MetricResult(1000, "test-result"))

      "notify principal" in {
        agent ! msg
        principal expectMsg msg
      }
    }
  }

  "A configured agent" when {
    val mconf = MetricConfiguration(testMetricId, 1000)
    val agent = TestActorRef(Agent.props(Seq(mconf)))

    "starts up" should {
      "create one collector" in {
        agent.children should have size 1
      }
    }

    "receives StartCollecting" when {
      "metric collection already exists" should {

        "change frequency to existing collector" in {
          agent ! StartCollecting(MetricConfiguration(testMetricId, 2000))
          agent.children.iterator.next ! GetFrequency

          agent.children should have size 1
          expectMsg(2000)
        }
      }

      "metric collection doesn't exist" should {
        val metricId = MetricId(testClassName, "another-param")

        "create new collector" in {
          agent ! StartCollecting(MetricConfiguration(metricId, 2000))

          agent.children should have size 2
        }
      }
    }

    "receives StopCollecting" when {
      "metric collection exists" should {
        "remove collector" in {
          agent ! StopCollecting(testMetricId)
          agent ! GetCollectors
          val collectors =
            receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

          collectors should have size 1
        }
      }

      "metric collection doesn't exist" should {
        "do nothing" in {
          agent ! StopCollecting(MetricId(testClassName, "more-params"))
          agent ! GetCollectors
          val collectors =
            receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

          collectors should have size 1
        }
      }
    }

    "receives ResetCollectors" should {
      "set new collector" in {
        val id = MetricId(testClassName, "reset-params")
        agent ! ResetCollectors(Seq(MetricConfiguration(id, 1000)))
        agent ! GetCollectors
        val collectors =
          receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

        collectors should have size 1
        collectors.contains(id) shouldBe true
      }
    }

    "receives StopAllCollectors" should {
      "remove all collectors" in {
        agent ! StopAllCollectors
        agent ! GetCollectors
        val collectors =
          receiveOne(1.second).asInstanceOf[Map[MetricId, ActorRef]]

        collectors shouldBe empty
      }
    }
  }
}
