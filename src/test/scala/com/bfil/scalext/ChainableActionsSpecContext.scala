package com.bfil.scalext

import org.specs2.execute.AsResult
import org.specs2.matcher.{ResultMatchers, ThrownExpectations}
import org.specs2.mock.Mockito
import org.specs2.mock.mockito.ArgThat
import org.specs2.mutable.Around

import com.bfil.scalext.testkit.ContextualDslTestkit

trait ChainableActionsSpecContext extends Around with Mockito with ArgThat with ThrownExpectations with ResultMatchers with ContextualDslTestkit[TestContext] {
  implicit val defautContext = TestContext("test", 0)
  def around[T: AsResult](t: => T) = AsResult(t)
}