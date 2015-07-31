package com.mooveit.moonitor.principal

import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.common.dto.AgentConfiguration
import com.mooveit.moonitor.principal.actors.Monitor.Check
import com.mooveit.moonitor.principal.actors.Principal
import com.mooveit.moonitor.principal.actors.Repository.Save
import com.mooveit.moonitor.principal.spec.UnitSpec

import scala.concurrent.duration._

class TestPrincipal extends UnitSpec("testPrincipal") {

  val probeRepository = TestProbe()
  val probeMonitor = TestProbe()
  val configuration = AgentConfiguration("localhost",
    probeRepository.ref, 1.second, probeMonitor.ref)

  val principal = TestActorRef(Principal.props(configuration))

  "A principal" when {
    "created" should {
      "spawn agent" in {
        principal.getChild(Iterator("agent")) shouldNot be(null)
      }
    }
    "status updated" should {
      "notify repository" in {
        probeRepository.expectMsgClass(classOf[Save])
      }
      "notify monitor" in {
        probeMonitor.expectMsgClass(classOf[Check])
      }
    }
  }
}
