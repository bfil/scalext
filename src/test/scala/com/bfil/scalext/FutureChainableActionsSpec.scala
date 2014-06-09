package com.bfil.scalext

import scala.concurrent.Future
import scala.util.{Failure, Success}

import com.bfil.scalext.actions.FutureChainableActions

class FutureChainableActionsSpec extends ChainableActionsSpec with FutureChainableActions[SpecsContext] {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def sayHello = Future { "hello" }
  def throwAnException = Future { throw new Exception("Something went wrong") }

  def sayHelloUsingContext(ctx: SpecsContext) = Future { s"hello ${ctx.key}" }
  def throwAnExceptionUsingContext(ctx: SpecsContext) = Future {
    throw new Exception(s"Something went wrong with error code: ${ctx.value}")
  }

  "onComplete" should "call the inner action with the expected future result on success" in {
    start {
      onComplete(sayHello) {
        case Success(str) =>
          str should be("hello")
          done
        case Failure(ex) =>
          fail
          done
      }
    }
  }

  it should "call the inner action with the expected future result on failure" in {
    start {
      onComplete(throwAnException) {
        case Success(str) =>
          fail
          done
        case Failure(ex) =>
          ex.getMessage should be("Something went wrong")
          done
      }
    }
  }

  it should "call the inner action with the expected future result on success (with context)" in {
    start {
      onComplete { ctx: SpecsContext =>
        sayHelloUsingContext(ctx)
      } {
        case Success(str) =>
          str should be("hello test")
          done
        case Failure(ex) =>
          fail
          done
      }
    }
  }

  it should "call the inner action with the expected future result on failure (with context)" in {
    start {
      onComplete { ctx: SpecsContext =>
        throwAnExceptionUsingContext(ctx)
      } {
        case Success(str) =>
          fail
          done
        case Failure(ex) =>
          ex.getMessage should be("Something went wrong with error code: 0")
          done
      }
    }
  }

  "onSuccess" should "call the inner action with the expected future result on success" in {
    start {
      onSuccess(sayHello) { str =>
        str should be("hello")
        done
      }
    }
  }

  it should "not call the inner action on failure" in {
    start {
      onSuccess(throwAnException) { str =>
        fail
        done
      }
    }
  }
  
  it should "call the inner action with the expected future result on success (with context)" in {
    start {
      onSuccess { ctx: SpecsContext =>
        sayHelloUsingContext(ctx)
      } { str =>
        str should be("hello test")
        done
      }
    }
  }

  it should "not call the inner action on failure (with context)" in {
    start {
      onSuccess { ctx: SpecsContext =>
        throwAnExceptionUsingContext(ctx)
      } { str =>
        fail
        done
      }
    }
  }

  "onFailure" should "call the inner action with the exception thrown on failure" in {
    start {
      onFailure(throwAnException) { ex =>
        ex.getMessage should be("Something went wrong")
        done
      }
    }
  }

  it should "not call the inner action on success" in {
    start {
      onFailure(sayHello) { ex =>
        fail
        done
      }
    }
  }
  
  it should "call the inner action with the exception thrown on failure (with context)" in {
    start {
      onFailure { ctx: SpecsContext =>
        throwAnExceptionUsingContext(ctx)
      } { ex =>
        ex.getMessage should be("Something went wrong with error code: 0")
        done
      }
    }
  }

  it should "not call the inner action on success (with context)" in {
    start {
      onFailure { ctx: SpecsContext =>
        sayHelloUsingContext(ctx)
      } { ex =>
        fail
        done
      }
    }
  }
}