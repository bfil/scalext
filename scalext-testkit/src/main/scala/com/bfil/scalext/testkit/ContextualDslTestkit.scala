package com.bfil.scalext.testkit

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.control.NonFatal

import com.bfil.scalext.ChainableActions

trait ContextualDslTestkit[T] extends ChainableActions[T] {

  implicit class RichChainableAction0(action: ChainableAction0) {
    def check(assertions: Action)(implicit ctx: Context) = action {
      ctx => assertions(ctx)
    }(ctx)

    def checkAsync(assertions: Action)(implicit ctx: Context, ec: ExecutionContext) = {
      val p = scala.concurrent.Promise[Unit]
      val f = p.future
      action { ctx =>
        try {
          assertions(ctx)
          p.completeWith(Future { () })
        } catch {
          case NonFatal(ex) => p.failure(ex)
        }
      }(ctx)
      CheckAsyncResult(f)
    }
  }

  implicit class RichChainableAction1[T](action: ChainableAction1[T]) {
    def check(assertions: T => Action)(implicit ctx: Context) =
      action { t =>
        ctx => assertions(t)(ctx)
      }(ctx)

    def checkAsync(assertions: T => Action)(implicit ctx: Context, ec: ExecutionContext) = {
      val p = scala.concurrent.Promise[Unit]
      val f = p.future
      action { t =>
        ctx =>
          try {
            assertions(t)(ctx)
            p.completeWith(Future { () })
          } catch {
            case NonFatal(ex) => p.failure(ex)
          }
      }(ctx)
      CheckAsyncResult(f)
    }
  }

  case class CheckAsyncResult(f: Future[Unit]) {
    def await(implicit duration: Duration = 3 seconds): Unit = Await.result(f, duration)
  }
}
