package com.mooveit.moonitor.principal

import akka.testkit.{TestActorRef, TestProbe}
import com.mooveit.moonitor.common.dto.MachineStatus
import com.mooveit.moonitor.principal.actors.Monitor
import com.mooveit.moonitor.principal.actors.Monitor.Check
import com.mooveit.moonitor.principal.actors.Notifier.FreeMemoryWarning
import com.mooveit.moonitor.principal.spec.UnitSpec

class TestMonitor extends UnitSpec("testMonitor") {

  val probeNotifier = TestProbe()

  val monitor = TestActorRef(Monitor.props(probeNotifier.ref))

  "A monitor" when {
    "threshold trepassed" should {
      "send warning to notifier" in {
        val status = MachineStatus("localhost", 0, 0, 0, 0, List())
        monitor ! Check("localhost", status)

        probeNotifier.expectMsg(FreeMemoryWarning("localhost", status))
      }
    }

    "threshold not trepassed" should {
      "do nothing" in {
        val status = MachineStatus("localhost", 0, 10, 0, 0, List())
        monitor ! Check("localhost", status)

        probeNotifier.expectNoMsg()
      }
    }
  }
}
