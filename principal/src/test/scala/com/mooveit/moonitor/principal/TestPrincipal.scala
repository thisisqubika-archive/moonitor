package com.mooveit.moonitor.principal

import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.domain.metrics.{MaxFiles, MetricResult}
import com.mooveit.moonitor.principal.actors.Agent.MetricCollected
import com.mooveit.moonitor.principal.actors.Principal
import com.mooveit.moonitor.principal.actors.MetricsStore.Save
import com.mooveit.moonitor.principal.spec.UnitSpec

class TestPrincipal extends UnitSpec("testPrincipal") {

  val repository = TestProbe()

  val principal = TestActorRef(Principal.props("localhost", repository.ref))

  "A principal" when {
    "created" should {
      "spawn agent" in {
        principal.getChild(Iterator("agent")) shouldNot be(null)
      }
    }
    "metric collected" should {
      principal ! MetricCollected(12345L, MetricResult(MaxFiles, 1))
      "notify repository" in {
        repository.expectMsgClass(classOf[Save])
      }
    }
  }
}
