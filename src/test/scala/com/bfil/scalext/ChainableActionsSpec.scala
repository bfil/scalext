package com.bfil.scalext

import scala.concurrent.Future

import org.specs2.execute.AsResult
import org.specs2.matcher.{ResultMatchers, ThrownExpectations}
import org.specs2.mock.Mockito
import org.specs2.mock.mockito.ArgThat
import org.specs2.mutable.{Around, Specification}
import org.specs2.time.NoTimeConversions

import com.bfil.scalext.actions.{BasicChainableActions, FutureChainableActions}

trait ChainableActionsSpec extends Specification with NoTimeConversions

trait ChainableActionsSpecContext extends Around with Mockito with ArgThat with ThrownExpectations with ResultMatchers {
  def around[T: AsResult](t: => T) = {
    AsResult(t)
  }
}

trait BasicChainableActionsSpecContext extends BasicChainableActions[TestContext] with ChainableActionsSpecContext {
  def start(action: Action) = action(TestContext("test", 0))
  def start(context: TestContext)(action: Action) = action(context)
  def check(action: Action) = ActionResult { ctx => action(ctx) }
}

trait FutureChainableActionsSpecContext extends FutureChainableActions[TestContext] with ChainableActionsSpecContext {
  def start(action: Action) = action(TestContext("test", 0))
  def start(context: TestContext)(action: Action) = action(context)
  def check(action: Action) = ActionResult { ctx => action(ctx) }

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def sayHello = Future { "hello" }
  def throwAnException = Future { throw new Exception("Something went wrong") }

  def sayHelloUsingContext(ctx: TestContext) = Future { s"hello ${ctx.key}" }
  def throwAnExceptionUsingContext(ctx: TestContext) = Future {
    throw new Exception(s"Something went wrong with error code: ${ctx.value}")
  }
}