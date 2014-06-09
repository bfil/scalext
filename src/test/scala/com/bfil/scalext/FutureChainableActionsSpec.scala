package com.bfil.scalext

import scala.concurrent.Future
import scala.util.{Failure, Success}

import com.bfil.scalext.actions.FutureChainableActions

class FutureChainableActionsSpec extends ChainableActionsSpec with FutureChainableActions[SpecsContext] {

  implicit val executionContext = scala.concurrent.ExecutionContext.global

  def sayHello = Future { "hello" }
  def throwAnException = Future { throw new Exception("Something went wrong") }

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
}