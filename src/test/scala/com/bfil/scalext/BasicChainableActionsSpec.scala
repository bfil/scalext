package com.bfil.scalext

class BasicChainableActionsSpec extends ChainableActionsSpec {

  "pass" should {
    "call the inner action without changing the context" in new BasicChainableActionsSpecContext {
      start {
        pass {
          check { ctx =>
            ctx.key must beEqualTo("test")
            ctx.value must beEqualTo(0)
          }
        }
      }
    }
  }

  "mapContext" should {
    "create a new context and pass it to the inner action" in new BasicChainableActionsSpecContext {
      def increaseValue(ctx: TestContext) = ctx.copy(value = ctx.value + 1)

      start {
        mapContext(increaseValue) {
          check { ctx =>
            ctx.key must beEqualTo("test")
            ctx.value must beEqualTo(1)
          }
        }
      }
    }
  }

  "provide" should {
    "provide a custom value to the inner action" in new BasicChainableActionsSpecContext {
      start {
        provide("a string") { str =>
          ctx => str must beEqualTo("a string")
        }
      }
    }
  }

  "extract" should {
    "extract some value from the context and pass it to the inner action" in new BasicChainableActionsSpecContext {

      def getValue(ctx: TestContext) = ctx.value

      start {
        extract(getValue) { value =>
          ctx => value must beEqualTo(0)
        }
      }
    }
  }
}