package com.bfil.scalext

import shapeless.{:: => ::, HList, HNil}

trait ApplyConverters[T] extends ContextualTypes[T] {
  abstract class ApplyConverter[L <: HList] {
    type In
    def apply(f: In): L => Action
  }

  object ApplyConverter {
    implicit val hac0 = new ApplyConverter[HNil] {
      type In = Action
      def apply(fn: In) = {
        case HNil => fn
      }
    }
    implicit def hac1[T1] = new ApplyConverter[::[T1, HNil]] {
      type In = T1 => Action
      def apply(fn: In) = {
        case t1 :: HNil => fn(t1)
      }
    }
    implicit def hac2[T1,T2] = new ApplyConverter[::[T1, ::[T2, HNil]]] {
      type In = (T1, T2) => Action
      def apply(fn: In) = {
        case t1 :: t2 :: HNil => fn(t1, t2)
      }
    }
  }
}