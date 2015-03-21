package com.bfil.scalext.actions

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait FutureChainableActions[T] extends BasicChainableActions[T] {
  def onComplete[T](magnet: OnCompleteMagnet[T]): ChainableAction1[Try[T]] = magnet.action

  trait OnCompleteMagnet[T] {
    val action: ChainableAction1[Try[T]]
  }

  object OnCompleteMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnCompleteMagnet[T] {
      val action: ChainableAction1[Try[T]] = ChainableAction { inner =>
        ctx =>
          f.onComplete {
            case result => inner(Tuple1(result))(ctx)
          }
      }
    }
    implicit def apply[T](f: Context => Future[T])(implicit ec: ExecutionContext) = new OnCompleteMagnet[T] {
      val action: ChainableAction1[Try[T]] = ChainableAction { inner =>
        ctx =>
          f(ctx).onComplete {
            case result => inner(Tuple1(result))(ctx)
          }
      }
    }
  }

  def onSuccess[T](magnet: OnSuccessMagnet[T]): ChainableAction1[T] = magnet.action

  trait OnSuccessMagnet[T] {
    val action: ChainableAction1[T]
  }

  object OnSuccessMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnSuccessMagnet[T] {
      val action: ChainableAction1[T] = ChainableAction { inner =>
        ctx =>
          f.onSuccess {
            case result => inner(Tuple1(result))(ctx)
          }
      }
    }
    implicit def apply[T](f: Context => Future[T])(implicit ec: ExecutionContext) = new OnSuccessMagnet[T] {
      val action: ChainableAction1[T] = ChainableAction { inner =>
        ctx =>
          f(ctx).onSuccess {
            case result => inner(Tuple1(result))(ctx)
          }
      }
    }
  }

  def onFailure[T](magnet: OnFailureMagnet): ChainableAction1[Throwable] = magnet.action

  trait OnFailureMagnet {
    val action: ChainableAction1[Throwable]
  }

  object OnFailureMagnet {
    implicit def apply[T](f: => Future[T])(implicit ec: ExecutionContext) = new OnFailureMagnet {
      val action: ChainableAction1[Throwable] = ChainableAction { inner =>
        ctx =>
          f.onFailure {
            case ex => inner(Tuple1(ex))(ctx)
          }
      }
    }
    implicit def apply[T](f: Context => Future[T])(implicit ec: ExecutionContext) = new OnFailureMagnet {
      val action: ChainableAction1[Throwable] = ChainableAction { inner =>
        ctx =>
          f(ctx).onFailure {
            case ex => inner(Tuple1(ex))(ctx)
          }
      }
    }
  }
}