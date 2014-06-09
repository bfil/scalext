package com.bfil.scalext.actions

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import shapeless.{:: => ::, HNil}

trait FutureChainableActions[T] extends BasicChainableActions[T] {
  def onComplete[T](magnet: OnCompleteMagnet[T]): ChainableAction1[Try[T]] = magnet

  trait OnCompleteMagnet[T] extends ChainableAction1[Try[T]]

  object OnCompleteMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnCompleteMagnet[T] {
      def happly(inner: Try[T] :: HNil => Action) = { ctx =>
        f.onComplete {
          case result => inner(result :: HNil)(ctx)
        }
      }
    }
  }

  def onSuccess[T](magnet: OnSuccessMagnet[T]): ChainableAction1[T] = magnet

  trait OnSuccessMagnet[T] extends ChainableAction1[T]

  object OnSuccessMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnSuccessMagnet[T] {
      def happly(inner: T :: HNil => Action) = { ctx =>
        f.onSuccess {
          case result => inner(result :: HNil)(ctx)
        }
      }
    }
  }
  
  def onFailure[T](magnet: OnFailureMagnet): ChainableAction1[Throwable] = magnet

  trait OnFailureMagnet extends ChainableAction1[Throwable]

  object OnFailureMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnFailureMagnet {
      def happly(inner: Throwable :: HNil => Action) = { ctx =>
        f.onFailure {
          case ex => inner(ex :: HNil)(ctx)
        }
      }
    }
  }
}