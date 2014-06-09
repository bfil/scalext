package com.bfil.scalext

import scala.concurrent.Future

import com.bfil.scalext.actions.FutureChainableActions

class FutureChainableActionsSpec  extends ChainableActionsSpec with FutureChainableActions[SpecsContext] {
  
  implicit val executionContext = scala.concurrent.ExecutionContext.global
  
  "onComplete" should "call the inner action with the future result" in {
    
    def sayHello = Future { "hello" }
    def onHello: ChainableAction1[String] = onComplete(sayHello)
    
    start {
      onHello { str =>
        str should be ("hello")
        done
      }
    }
  }
}