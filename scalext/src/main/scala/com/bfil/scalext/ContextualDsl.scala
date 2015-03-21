package com.bfil.scalext

import com.bfil.scalext.actions.{ActorChainableActions, BasicChainableActions, FutureChainableActions}

trait ContextualDsl[T] extends BasicChainableActions[T] with FutureChainableActions[T] with ActorChainableActions[T]