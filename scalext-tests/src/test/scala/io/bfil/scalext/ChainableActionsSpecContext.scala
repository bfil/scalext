package io.bfil.scalext

import io.bfil.scalext.testkit.ContextualDslTestkit
import org.specs2.execute.AsResult
import org.specs2.matcher.{Expectations, ThrownExpectations}
import org.specs2.mock._
import org.specs2.mock.mockito._
import org.specs2.specification.Scope

trait ChainableActionsSpecContext extends Scope
  with MockitoWithNoCalledMatchers with ThrownExpectations
  with ContextualDslTestkit[TestContext] {
  implicit val defautContext = TestContext("test", 0)
}

trait MockitoWithNoCalledMatchers extends MocksCreation
  with MockitoStubs
  with CapturedArgument
  with MockitoMatchers
  with ArgThat
  with Expectations
  with MockitoFunctions
