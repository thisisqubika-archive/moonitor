package com.mooveit.moonitor.principal.spec

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class UnitSpec(_system: ActorSystem) extends TestKit(_system)
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this(systemName: String) = this(ActorSystem(systemName))

  override protected def afterAll() = {
    TestKit.shutdownActorSystem(_system)
  }
}
