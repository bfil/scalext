package com.bfil.scalext.testkit

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, DurationInt}

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

    implicit def fromChainableAction0(action: ChainableAction0)(implicit ctx: Context) =
      new CheckMagnet {
        type Result = CheckMagnet0
        def apply() = new CheckMagnet0 {
          def apply(assertions: Action) = {
            action {
              ctx => assertions(ctx)
            }(ctx)
          }
        }
      }

    implicit def fromChainableAction1[T](action: ChainableAction1[T])(implicit ctx: Context) =
      new CheckMagnet {
        type Result = CheckMagnet1[T]
        def apply() = new CheckMagnet1[T] {
          def apply(assertions: T => Action) = {
            action { t =>
              ctx => assertions(t)(ctx)
            }(ctx)
          }
        }
      }
  }

  def asyncCheck(magnet: AsyncCheckMagnet): magnet.Result = magnet()

  trait AsyncCheckMagnet {
    type Result
    def apply(): Result
  }

  trait AsyncCheckMagnet0 {
    def apply(assertions: Action): Unit
  }

  trait AsyncCheckMagnet1[T] {
    def apply(assertions: T => Action): Unit
  }

  object AsyncCheckMagnet {

    implicit def fromChainableAction0(action: ChainableAction0)(implicit ctx: Context, ec: ExecutionContext, d: Duration = 3 seconds) =
      new AsyncCheckMagnet {
        type Result = AsyncCheckMagnet0
        def apply() = new AsyncCheckMagnet0 {
          def apply(assertions: Action) = {
            val p = scala.concurrent.Promise[Unit]
            val f = p.future
            action { ctx =>
              try {
                assertions(ctx)
                p.completeWith(Future { () })
              } catch {
                case ex: Throwable => p.failure(ex)
              }
            }(ctx)
            Await.result(f, d)
          }
        }
      }

    implicit def fromChainableAction1[T](action: ChainableAction1[T])(implicit ctx: Context, ec: ExecutionContext, d: Duration = 3 seconds) =
      new AsyncCheckMagnet {
        type Result = AsyncCheckMagnet1[T]
        def apply() = new AsyncCheckMagnet1[T] {
          def apply(assertions: T => Action) = {
            val p = scala.concurrent.Promise[Unit]
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
            Await.result(f, d)
          }
        }
      }
  }

  case class CheckWaiter(f: Future[Unit]) {
    def wait(implicit duration: Duration = 3 seconds) = Await.result(f, duration)
  }

  def futureCheck(magnet: FutureCheckMagnet): magnet.Result = magnet()

  trait FutureCheckMagnet {
    type Result
    def apply(): Result
  }

  trait FutureCheckMagnet0 {
    def apply(assertions: Action): CheckWaiter
  }

  trait FutureCheckMagnet1[T] {
    def apply(assertions: T => Action): CheckWaiter
  }

  object FutureCheckMagnet {

    implicit def fromChainableAction0(action: ChainableAction0)(implicit ctx: Context, ec: ExecutionContext) =
      new FutureCheckMagnet {
        type Result = FutureCheckMagnet0
        def apply() = new FutureCheckMagnet0 {
          def apply(assertions: Action) = {
            val p = scala.concurrent.Promise[Unit]
            val f = p.future
            action { ctx =>
              try {
                assertions(ctx)
                p.completeWith(Future { () })
              } catch {
                case ex: Throwable => p.failure(ex)
              }
            }(ctx)
            CheckWaiter(f)
          }
        }
      }

    implicit def fromChainableAction1[T](action: ChainableAction1[T])(implicit ctx: Context, ec: ExecutionContext, d: Duration = 3 seconds) =
      new FutureCheckMagnet {
        type Result = FutureCheckMagnet1[T]
        def apply() = new FutureCheckMagnet1[T] {
          def apply(assertions: T => Action) = {
            val p = scala.concurrent.Promise[Unit]
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
            CheckWaiter(f)
          }
        }
      }
  }
}