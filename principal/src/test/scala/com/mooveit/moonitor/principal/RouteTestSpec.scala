package com.mooveit.moonitor.principal

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpecLike}
import spray.testkit.ScalatestRouteTest

class RouteTestSpec extends WordSpecLike with Matchers with ScalatestRouteTest {


  override def testConfig = ConfigFactory.load("test-application")
}
