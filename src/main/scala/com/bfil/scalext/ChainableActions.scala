package com.bfil.scalext

import shapeless.{:: => ::, HList, HNil}

trait ChainableActions[T] extends ApplyConverters[T] {
  abstract class ChainableAction[L <: HList] { self =>
    def happly(f: L => Action): Action
  }

  object ChainableAction {
    object Empty extends ChainableAction0 {
      def happly(inner: HNil => Action) = inner(HNil)
    }
    implicit def pimpApply[L <: HList](ChainableAction: ChainableAction[L])(implicit hac: ApplyConverter[L]): hac.In => Action =
      f => ChainableAction.happly(hac(f))
  }

  type ChainableAction0 = ChainableAction[HNil]
  type ChainableAction1[T] = ChainableAction[T :: HNil]
  
  object Action {
    def apply(f: Action): Action = f
    def toChainableAction[L <: HList](scrapeAction: Action): ChainableAction[L] = new ChainableAction[L] {
      def happly(f: L => Action) = scrapeAction
    }
  }

  abstract class ActionResult extends Action {
    def toChainableAction[L <: HList]: ChainableAction[L] = ActionResult.toChainableAction(this)
  }

  object ActionResult {
    def apply(scrapeAction: Action): ActionResult = scrapeAction match {
      case x: ActionResult => x
      case x         => new ActionResult { def apply(ctx: Context): Unit = { x(ctx) } }
    }
    implicit def toChainableAction[L <: HList](scrapeAction: Action): ChainableAction[L] = Action.toChainableAction(scrapeAction)
  }
}