package io.bfil.scalext

sealed trait Tuple[T]

object Tuple {
  implicit def forNothing[A]: Tuple[Nothing] = null
  implicit def forUnit[A]: Tuple[Unit] = null
  implicit def forTuple1[A]: Tuple[Tuple1[A]] = null
  implicit def forTuple2[A, B]: Tuple[(A, B)] = null
}