package io.bfil.scalext

import java.util.concurrent.TimeoutException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

import org.specs2.mutable.Specification

class ActorChainableActionsSpec extends Specification {

  "after" should {
    "compile" in new ActorChainableActionsSpecContext {
      after(500 millis) {
        ctx => success
      }
    }

    "call the inner action after the given delay" in new ActorChainableActionsSpecContext {
      val start = System.currentTimeMillis
      after(500 millis).checkAsync {
        ctx =>
          val elapsed = System.currentTimeMillis - start
          elapsed.toInt must beGreaterThan(500)
      }.await must not(throwA[TimeoutException])
    }

    "throw a timeout exception with a delay > 1 second" in new ActorChainableActionsSpecContext {
      implicit val d = 1.second
      after(1500 millis).checkAsync {
        ctx => failure
      }.await must throwA[TimeoutException]
    }
  }
}
