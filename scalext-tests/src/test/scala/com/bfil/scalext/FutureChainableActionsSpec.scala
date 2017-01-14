package com.bfil.scalext

import java.util.concurrent.TimeoutException

import scala.util.{Failure, Success}

import org.specs2.mutable.Specification

class FutureChainableActionsSpec extends Specification {

  "onComplete" should {
    "compile" in new FutureChainableActionsSpecContext {
      onComplete(sayHello) {
        case Success(str) => ctx => success
        case Failure(str) => ctx => failure
      }

      onComplete({ ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }) {
        case Success(str) => ctx => success
        case Failure(str) => ctx => failure
      }
    }

    "call the inner action with the expected future result on success" in new FutureChainableActionsSpecContext {
      onComplete(sayHello) checkAsync {
        case Success(str) => ctx => str must beEqualTo("hello")
        case Failure(ex) => ctx => failure
      } await
    }

    "call the inner action with the expected future result on failure" in new FutureChainableActionsSpecContext {
      onComplete(throwAnException) checkAsync {
        case Success(str) => ctx => failure
        case Failure(ex) => ctx => ex.getMessage must beEqualTo("Something went wrong")
      } await
    }

    "call the inner action with the expected future result on success (with context)" in new FutureChainableActionsSpecContext {
      onComplete { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      } checkAsync {
        case Success(str) => ctx => str must beEqualTo("hello test")
        case Failure(ex) => ctx => failure
      } await
    }

    "call the inner action with the expected future result on failure (with context)" in new FutureChainableActionsSpecContext {
      onComplete { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      } checkAsync {
        case Success(str) => ctx => failure
        case Failure(ex) => ctx => ex.getMessage must beEqualTo("Something went wrong with error code: 0")
      } await
    }
  }

  "onSuccess" should {
    "compile" in new FutureChainableActionsSpecContext {
      onSuccess(sayHello) { str =>
        ctx => success
      }

      onSuccess({ ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }) { str =>
        ctx => success
      }
    }

    "call the inner action with the expected future result on success" in new FutureChainableActionsSpecContext {
      onSuccess(sayHello) checkAsync { str =>
        ctx => str must beEqualTo("hello")
      } await
    }

    "not call the inner action on failure" in new FutureChainableActionsSpecContext {
      onSuccess(throwAnException).checkAsync { str =>
        ctx => failure
      }.await must throwA[TimeoutException]
    }

    "call the inner action with the expected future result on success (with context)" in new FutureChainableActionsSpecContext {
      onSuccess { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      } checkAsync { str =>
        ctx => str must beEqualTo("hello test")
      } await
    }

    "not call the inner action on failure (with context)" in new FutureChainableActionsSpecContext {
      onSuccess { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      }.checkAsync { str =>
        ctx => failure
      }.await must throwA[TimeoutException]
    }
  }

  "onFailure" should {
    "compile" in new FutureChainableActionsSpecContext {
      onFailure(throwAnException) { ex =>
        ctx => success
      }

      onFailure({ ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      }) checkAsync { ex =>
        ctx => success
      } await
    }

    "call the inner action with the exception thrown on failure" in new FutureChainableActionsSpecContext {
      onFailure(throwAnException) checkAsync { ex =>
        ctx => ex.getMessage must beEqualTo("Something went wrong")
      } await
    }

    "not call the inner action on success" in new FutureChainableActionsSpecContext {
      onFailure(sayHello).checkAsync { ex =>
        ctx => failure
      }.await must throwA[TimeoutException]
    }

    "call the inner action with the exception thrown on failure (with context)" in new FutureChainableActionsSpecContext {
      onFailure { ctx: TestContext =>
        throwAnExceptionUsingContext(ctx)
      } checkAsync { ex =>
        ctx => ex.getMessage must beEqualTo("Something went wrong with error code: 0")
      } await
    }

    "not call the inner action on success (with context)" in new FutureChainableActionsSpecContext {
      onFailure { ctx: TestContext =>
        sayHelloUsingContext(ctx)
      }.checkAsync { ex =>
        ctx => failure
      }.await must throwA[TimeoutException]
    }
  }
}
