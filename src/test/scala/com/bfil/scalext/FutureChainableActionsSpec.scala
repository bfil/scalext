package com.bfil.scalext

import scala.util.{ Failure, Success }
import java.util.concurrent.TimeoutException

class FutureChainableActionsSpec extends ChainableActionsSpec {

  "onComplete" should {
    "call the inner action with the expected future result on success" in new FutureChainableActionsSpecContext {
      asyncCheck(onComplete(sayHello)) {
        case Success(str) => ctx => str must beEqualTo("hello")
        case Failure(ex) => ctx => failure
      }
    }

    "call the inner action with the expected future result on failure" in new FutureChainableActionsSpecContext {
      asyncCheck(onComplete(throwAnException)) {
        case Success(str) => ctx => failure
        case Failure(ex) => ctx => ex.getMessage must beEqualTo("Something went wrong")
      }
    }

    "call the inner action with the expected future result on success (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onComplete { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }) {
        case Success(str) => ctx => str must beEqualTo("hello test")
        case Failure(ex) => ctx => failure
      }
    }

    "call the inner action with the expected future result on failure (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onComplete { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      }) {
        case Success(str) => ctx => failure
        case Failure(ex) => ctx => ex.getMessage must beEqualTo("Something went wrong with error code: 0")
      }
    }
  }

  "onSuccess" should {
    "call the inner action with the expected future result on success" in new FutureChainableActionsSpecContext {
      asyncCheck(onSuccess(sayHello)) { str =>
        ctx => str must beEqualTo("hello")
      }
    }

    "not call the inner action on failure" in new FutureChainableActionsSpecContext {
      asyncCheck(onSuccess(throwAnException)) { str =>
        ctx => failure
      } must throwA[TimeoutException]
    }

    "call the inner action with the expected future result on success (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onSuccess { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }) { str =>
        ctx => str must beEqualTo("hello test")
      }
    }

    "not call the inner action on failure (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onSuccess { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      }) { str =>
        ctx => failure
      } must throwA[TimeoutException]
    }
  }

  "onFailure" should {
    "call the inner action with the exception thrown on failure" in new FutureChainableActionsSpecContext {
      asyncCheck(onFailure(throwAnException)) { ex =>
        ctx => ex.getMessage must beEqualTo("Something went wrong")
      }
    }

    "not call the inner action on success" in new FutureChainableActionsSpecContext {
      asyncCheck(onFailure(sayHello)) { ex =>
        ctx => failure
      } must throwA[TimeoutException]
    }

    "call the inner action with the exception thrown on failure (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onFailure { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      }) { ex =>
        ctx => ex.getMessage must beEqualTo("Something went wrong with error code: 0")
      }
    }

    "not call the inner action on success (with context)" in new FutureChainableActionsSpecContext {
      asyncCheck(onFailure { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }) { ex =>
        ctx => failure
      } must throwA[TimeoutException]
    }
  }
}