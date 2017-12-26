package io.bfil.scalext

import scala.concurrent.Future

import io.bfil.scalext.actions.FutureChainableActions

trait FutureChainableActionsSpecContext extends FutureChainableActions[TestContext] with ChainableActionsSpecContext {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def sayHello: Future[String] = Future { "hello" }
  def throwAnException: Future[Unit] = Future { throw new Exception("Something went wrong") }

  def sayHelloUsingContext(ctx: TestContext): Future[String] = Future { s"hello ${ctx.key}" }
  def throwAnExceptionUsingContext(ctx: TestContext): Future[Unit] = Future {
    throw new Exception(s"Something went wrong with error code: ${ctx.value}")
  }
}