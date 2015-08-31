package com.mooveit.moonitor.agent

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

abstract class UnitSpec(_system: ActorSystem) extends TestKit(_system)
  with WordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  def this(systemName: String) =
    this(ActorSystem(systemName, ConfigFactory.load("test-application")))

  override protected def afterAll() = {
    TestKit.shutdownActorSystem(_system)
  }
}
