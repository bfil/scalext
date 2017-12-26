package io.bfil.scalext

import io.bfil.scalext.actions.{ActorChainableActions, BasicChainableActions, FutureChainableActions}

trait ContextualDsl[T] extends BasicChainableActions[T] with FutureChainableActions[T] with ActorChainableActions[T]