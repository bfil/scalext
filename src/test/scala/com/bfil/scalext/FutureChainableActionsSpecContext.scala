package com.bfil.scalext

import scala.concurrent.Future

import com.bfil.scalext.actions.FutureChainableActions

trait FutureChainableActionsSpecContext extends FutureChainableActions[TestContext] with ChainableActionsSpecContext {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def sayHello = Future { "hello" }
  def throwAnException = Future { throw new Exception("Something went wrong") }

  def sayHelloUsingContext(ctx: TestContext) = Future { s"hello ${ctx.key}" }
  def throwAnExceptionUsingContext(ctx: TestContext) = Future {
    throw new Exception(s"Something went wrong with error code: ${ctx.value}")
  }
}