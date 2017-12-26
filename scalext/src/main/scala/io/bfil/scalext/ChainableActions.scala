package io.bfil.scalext

trait ChainableActions[T] extends ApplyConverters[T] {

  abstract class ChainableAction[L](implicit val ev: Tuple[L]) {
    def tapply(f: L => Action): Action
  }

  object ChainableAction {
    object Empty extends ChainableAction0 {
      def tapply(inner: Unit => Action) = inner(())
    }

    implicit def pimpApplyN[L](chainableAction: ChainableAction[L])(implicit hac: ApplyConverter[L]): hac.In => Action =
      f => chainableAction.tapply(hac(f))

    implicit def pimpApply0(chainableAction: ChainableAction0): (=> Action) => Action =
      r => chainableAction.tapply(_ => r)

    def apply[T: Tuple](f: (T => Action) => Action): ChainableAction[T] =
      new ChainableAction[T] { def tapply(inner: T => Action) = f(inner) }
  }

  type ChainableAction0 = ChainableAction[Unit]
  type ChainableAction1[T] = ChainableAction[Tuple1[T]]

  object Action {
    def apply(f: Action): Action = f
    def toChainableAction[L: Tuple](action: Action) = ChainableAction[L] { _ => action }
  }

  abstract class ActionResult extends Action {
    def toChainableAction[L: Tuple]: ChainableAction[L] = ActionResult.toChainableAction(this)
  }

  object ActionResult {
    def apply(action: Action): ActionResult = action match {
      case x: ActionResult => x
      case x               => new ActionResult { def apply(ctx: Context) = { x(ctx) } }
    }
    implicit def toChainableAction[L: Tuple](action: Action): ChainableAction[L] = Action.toChainableAction(action)
  }
}