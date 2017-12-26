package io.bfil.scalext

trait ApplyConverters[T] extends ContextualTypes[T] {
  abstract class ApplyConverter[L] {
    type In
    def apply(f: In): L => Action
  }

  object ApplyConverter {
    implicit def hac1[T1] = new ApplyConverter[Tuple1[T1]] {
      type In = T1 => Action
      def apply(fn: In) = {
        case Tuple1(t1) => fn(t1)
      }
    }
    implicit def hac2[T1, T2] = new ApplyConverter[Tuple2[T1, T2]] {
      type In = (T1, T2) => Action
      def apply(fn: In) = {
        case Tuple2(t1, t2) => fn(t1, t2)
      }
    }
  }
}