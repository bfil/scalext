package com.bfil.scalext

trait ContextualTypes[T] {
  type Context = T
  type Action = Context => Unit
}