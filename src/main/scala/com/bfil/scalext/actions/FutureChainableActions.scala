package com.bfil.scalext.actions

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import shapeless.{:: => ::, HNil}

trait FutureChainableActions[T] extends BasicChainableActions[T] {
  def onComplete[T](f: => Future[T])(implicit ec: ExecutionContext): ChainableAction1[T] = new ChainableAction1[T] {
    def happly(inner: (T :: HNil) => Action) = { ctx =>
      f.onComplete {
        case Success(t)  => inner(t :: HNil)(ctx)
        case Failure(ex) => throw ex
      }
    }
  }

  def onComplete[T](f: Context => Future[T])(implicit ec: ExecutionContext): ChainableAction1[T] = new ChainableAction1[T] {
    def happly(inner: (T :: HNil) => Action) = { ctx =>
      f(ctx).onComplete {
        case Success(t)  => inner(t :: HNil)(ctx)
        case Failure(ex) => throw ex
      }
    }
  }
  
  def onSuccess[T](f: => Future[T])(implicit ec: ExecutionContext): ChainableAction1[T] = new ChainableAction1[T] {
    def happly(inner: (T :: HNil) => Action) = { ctx =>
      f.onSuccess {
        case t  => inner(t :: HNil)(ctx)
      }
    }
  }
  
  def onSuccess[T](f: Context => Future[T])(implicit ec: ExecutionContext): ChainableAction1[T] = new ChainableAction1[T] {
    def happly(inner: (T :: HNil) => Action) = { ctx =>
      f(ctx).onSuccess {
        case t  => inner(t :: HNil)(ctx)
      }
    }
  }
}