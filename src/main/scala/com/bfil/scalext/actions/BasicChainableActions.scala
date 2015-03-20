package com.bfil.scalext.actions

import com.bfil.scalext.{ChainableActions, Tuple}

trait BasicChainableActions[T] extends ChainableActions[T] {
  def pass: ChainableAction0 = ChainableAction.Empty

  def mapInnerAction(f: Action => Action): ChainableAction0 = ChainableAction { inner => f(inner(())) }
  def mapContext(f: Context => Context): ChainableAction0 = mapInnerAction { inner => ctx => inner(f(ctx)) }

  def provide[T](value: T): ChainableAction1[T] = tprovide(Tuple1(value))
  def tprovide[L: Tuple](values: L): ChainableAction[L] = ChainableAction { _(values) }

  def extract[T](f: Context => T): ChainableAction1[T] = textract(ctx => Tuple1(f(ctx)))
  def textract[L: Tuple](f: Context ⇒ L): ChainableAction[L] = ChainableAction { inner => ctx => inner(f(ctx))(ctx) }
}