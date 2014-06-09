package com.bfil.scalext.actions

import com.bfil.scalext.ChainableActions

import shapeless.{HList, HNil}

trait BasicChainableActions[T] extends ChainableActions[T] {
  def pass: ChainableAction0 = ChainableAction.Empty

  def mapInnerAction(f: Action => Action): ChainableAction0 = new ChainableAction0 {
    def happly(inner: HNil => Action) = f(inner(HNil))
  }

  def mapContext(f: Context => Context): ChainableAction0 = mapInnerAction { inner => ctx => inner(f(ctx)) }

  def provide[T](value: T): ChainableAction1[T] = hprovide(value :: HNil)
  def hprovide[L <: HList](values: L): ChainableAction[L] = new ChainableAction[L] {
    def happly(f: L => Action) = f(values)
  }

  def extract[T](f: Context => T): ChainableAction1[T] = hextract(ctx => f(ctx) :: HNil)
  def hextract[L <: HList](f: Context => L): ChainableAction[L] = new ChainableAction[L] {
    def happly(inner: L => Action) = ctx => inner(f(ctx))(ctx)
  }
}