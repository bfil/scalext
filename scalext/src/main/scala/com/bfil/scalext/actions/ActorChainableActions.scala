package com.bfil.scalext.actions

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

import akka.actor.ActorContext

trait ActorChainableActions[T] extends BasicChainableActions[T] {
  def after(magnet: AfterDelayMagnet): ChainableAction0 = magnet

  trait AfterDelayMagnet extends ChainableAction0

  object AfterDelayMagnet {
    implicit def apply[T](delay: FiniteDuration)(implicit ec: ExecutionContext, ac: ActorContext) = new AfterDelayMagnet {
      def tapply(inner: Unit => Action) = { ctx =>
        akka.pattern.after(delay, using = ac.system.scheduler)(Future { Unit }).onComplete {
          case _ => inner(())(ctx)
        }
      }
    }
  }
}