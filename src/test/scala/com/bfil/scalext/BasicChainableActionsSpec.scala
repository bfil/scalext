package com.bfil.scalext

import com.bfil.scalext.actions.BasicChainableActions

class BasicChainableActionsSpec extends ChainableActionsSpec with BasicChainableActions[SpecsContext] {

  "pass" should "call the inner action without changing the context" in {
    start {
      pass {
        check { ctx =>
          ctx.key should be("test")
          ctx.value should be(0)
        }
      }
    }
  }
  
  "mapContext" should "create a new context and pass it to the inner action" in {
    
    def increaseValue(ctx: SpecsContext) = ctx.copy(value = ctx.value + 1)
    
    start {
      mapContext(increaseValue) {         
        check { ctx =>
          ctx.key should be("test")
          ctx.value should be(1)
        }
      }
    }
  }
  
  "provide" should "provide a custom value to the inner action" in {    
    start {
      provide("a string") { str =>
        str should be("a string")
        done
      }
    }
  }
  
  "extract" should "extract some value from the context and pass it to the inner action" in {
    
    def getValue(ctx: SpecsContext) = ctx.value
    
    start {
      extract(getValue) { value =>
        value should be(0)
        done
      }
    }
  }
}