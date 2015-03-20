package com.bfil.scalext

class BasicChainableActionsSpec extends ChainableActionsSpec {

  "pass" should {
    "compile" in new BasicChainableActionsSpecContext {
      pass {
        ctx => success
      }
    }

    "call the inner action without changing the context" in new BasicChainableActionsSpecContext {
      check(pass) { ctx =>
        ctx.key must beEqualTo("test")
        ctx.value must beEqualTo(0)
      }
    }
  }

  "mapContext" should {
    "compile" in new BasicChainableActionsSpecContext {
      mapContext {
        ctx => ctx
      } {
        ctx => success
      }
    }

    "create a new context and pass it to the inner action" in new BasicChainableActionsSpecContext {
      def increaseValue(ctx: TestContext) = ctx.copy(value = ctx.value + 1)
      check(mapContext(increaseValue)) {
        ctx =>
          ctx.key must beEqualTo("test")
          ctx.value must beEqualTo(1)
      }
    }
  }

  "provide" should {
    "compile" in new BasicChainableActionsSpecContext {
      provide("a string") { str =>
        ctx => success
      }
    }

    "provide a custom value to the inner action" in new BasicChainableActionsSpecContext {
      check(provide("a string")) { str =>
        ctx => str must beEqualTo("a string")
      }
    }
  }

  "extract" should {
    "compile" in new BasicChainableActionsSpecContext {
      extract(_.value) { value =>
        ctx => success
      }
    }

    "extract some value from the context and pass it to the inner action" in new BasicChainableActionsSpecContext {
      def getValue(ctx: TestContext) = ctx.value
      check(extract(getValue)) { value =>
        ctx => value must beEqualTo(0)
      }
    }
  }
}