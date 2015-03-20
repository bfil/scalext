package com.bfil.scalext

import java.util.concurrent.TimeoutException

import scala.concurrent.duration.DurationInt

class ActorChainableActionsSpec extends ChainableActionsSpec {

  "after" should {
    "compile" in new ActorChainableActionsSpecContext {
      // needed to overcome ambiguous reference to overloaded definition
      override def after(magnet: AfterDelayMagnet): ChainableAction0 = magnet

      after(500 millis) {
        ctx => success
      }
    }

    "call the inner action after the given delay" in new ActorChainableActionsSpecContext {
      val start = System.currentTimeMillis
      asyncCheck(after(500 millis)) {
        ctx =>
          val elapsed = System.currentTimeMillis - start
          elapsed.toInt must beGreaterThan(500)
      } must not(throwA[TimeoutException])
    }

    "throw a timeout exception with a delay > 1 second" in new ActorChainableActionsSpecContext {
      implicit val d = 1.second
      asyncCheck(after(1500 millis)) {
        ctx => failure
      } must throwA[TimeoutException]
    }
  }
}