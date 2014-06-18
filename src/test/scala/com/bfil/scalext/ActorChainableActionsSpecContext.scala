package com.bfil.scalext

import com.bfil.scalext.actions.ActorChainableActions

import akka.actor.{ActorContext, ActorSystem}

trait ActorChainableActionsSpecContext extends ActorChainableActions[TestContext] with ChainableActionsSpecContext {
  implicit val actorSystem = ActorSystem("test-system")
  implicit val actorContext = mock[ActorContext]
  actorContext.system returns actorSystem
}