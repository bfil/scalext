package com.bfil.scalext

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ActorContext, ActorSystem}
import com.bfil.scalext.actions.ActorChainableActions

trait ActorChainableActionsSpecContext extends ActorChainableActions[TestContext] with ChainableActionsSpecContext {
  implicit val actorSystem = ActorSystem("test-system")
  implicit val actorContext = mock[ActorContext]
  actorContext.system returns actorSystem
}
