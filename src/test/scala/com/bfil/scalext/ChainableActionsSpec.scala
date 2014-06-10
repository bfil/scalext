package com.bfil.scalext

import org.scalatest.{FlatSpec, Matchers}

class ChainableActionsSpec extends FlatSpec with Matchers with ChainableActions[SpecsContext] {
  def start(action: Action) = action(SpecsContext("test", 0))
  def start(context: SpecsContext)(action: Action) = action(context)
  def check(action: Action) = ActionResult { ctx => action(ctx) }
  def done = ActionResult { ctx => Unit }
}