package com.bfil.scalext.testkit

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

import com.bfil.scalext.ChainableActions

trait ContextualDslTestkit[T] extends ChainableActions[T] {
  def check(magnet: CheckMagnet): magnet.Result = magnet()

  trait CheckMagnet {
    type Result
    def apply(): Result
  }

  trait CheckMagnet0 {
    def apply(assertions: Action): Unit
  }

  trait CheckMagnet1[T] {
    def apply(assertions: T => Action): Unit
  }

  object CheckMagnet {

    implicit def fromChainableAction0(action: ChainableAction0)(implicit ctx: Context, ec: ExecutionContext) =
      new CheckMagnet {
        type Result = CheckMagnet0
        def apply() = new CheckMagnet0 {
          def apply(assertions: Action) {
            val p = scala.concurrent.promise[Unit]
            val f = p.future
            action { ctx =>
              try {
                assertions(ctx)
                p.completeWith(Future { () })
              } catch {
                case ex: Throwable => p.failure(ex)
              }
            }(ctx)
            Await.result(f, 1 second)
          }
        }
      }

    implicit def fromChainableAction1[T](action: ChainableAction1[T])(implicit ctx: Context, ec: ExecutionContext) =
      new CheckMagnet {
        type Result = CheckMagnet1[T]
        def apply() = new CheckMagnet1[T] {
          def apply(assertions: T => Action) = {
            val p = scala.concurrent.promise[Unit]
            val f = p.future
            action { t =>
              ctx =>
                try {
                  assertions(t)(ctx)
                  p.completeWith(Future { () })
                } catch {
                  case ex: Throwable => p.failure(ex)
                }
            }(ctx)
            Await.result(f, 1 second)
          }
        }
      }
  }
}