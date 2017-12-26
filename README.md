Scalext
=======

[![Codacy Badge](https://www.codacy.com/project/badge/76913171ff464582bfeb83773d9bf873)](https://www.codacy.com/app/bfil/scalext)

A scala library that enable the creation of contextual DSLs, inspired by the routing module of [Spray](http://spray.io/).

It uses part of the internal code of [spray-routing](https://github.com/spray/spray/tree/master/spray-routing/src/main/scala/spray/routing) and its directives, but enables to use custom contexts and create a context-specific DSL.

Setting up the dependencies
---------------------------

__Scalext__ is available on `Maven Central` (since version `0.4.0`), and it is cross compiled and published for Scala 2.12, 2.11 and 2.10.

*Older artifacts versions are not available anymore due to the shutdown of my self-hosted Nexus Repository in favour of Bintray*

Using SBT, add the following dependency to your build file:

```scala
libraryDependencies ++= Seq(
  "io.bfil" %% "scalext" % "0.4.0"
)
```

If you have issues resolving the dependency, you can add the following resolver:

```scala
resolvers += Resolver.bintrayRepo("bfil", "maven")
```

Usage
-----

To build your own DSL all you need to do is:

 1. define your custom context object
 2. define the DSL actions

### A basic example

Let's use a simple calculator DSL as an example.

The calculator DSL context will be a simple class that stores a promise object (which will resolve into a future result at some point) and the current calculation value.

The `ArihmeticContext` object can be a simple case class:

```scala
case class ArithmeticContext(resultPromise: Promise[Double], value: Double)
```

The DSL actions will manipulate this context, we can put the actions in a `CalculatorDsl` trait:

```scala
trait CalculatorDsl extends ContextualDsl[ArithmeticContext] {
  // actions in here..
}
```

The first action triggering the DSL logic needs to accept an `Action` as a parameter.

In this example we create an initial action `startWith` that accepts an initial value, it creates the `ArithmeticContext` storing the initial value and a promise, and it passes the newly created context into the action specified.

```scala
def startWith(initialValue: Long)(action: Action) = {
  val p = Promise[Double]
  action(ArithmeticContext(p, initialValue))
  p.future
}
```

The above method returns the future associated with the promise, so that we can resolve the promise at a later point when we want to return a result.

Let's define some basic actions for the DSL (`add`, `subtract`, `multiplyBy`, and `divideBy`), the actions use the `mapContext` helper method of the `ContextualDsl` trait, which modifies the immutable context and passes it through to the inner action.

```scala
def add(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value + value))
def subtract(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value - value))
def multiplyBy(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value * value))
def divideBy(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value / value))
```

The inner most method of the DSL usually must be a simple action that takes the context object and returns Unit, what we want to do is completing the promise by passing in it the result of our calculation.

```scala
  def returnResult = ActionResult { ctx => ctx.resultPromise.completeWith(Future { ctx.value }) }
```

We called our final action `returnResult`, and we defined it using the apply method of the `ActionResult` object, which makes the code more readable, but it could just be a simple method with the signature `Context => Unit`.

Finally using our `CalculatorDsl` in our code will look like this:

```scala
class Calculator extends CalculatorDsl {
  def performCalculation: Future[Double] =
    startWith(2) {
      add(3) {
        multiplyBy(3) {
          subtract(5) {
            divideBy(2) {
              returnResult
            }
          }
        }
      }
    }
}
```

This is just a simple example of how the library can be used to create DSLs that manipulate a context.

### Documentation

The main components of __Scalext__ are the `ContextualDsl` trait and the DSL actions.

#### Contextual DSL

The `ContextualDsl` trait accepts a context type parameter

```scala
trait ContextualDsl[T]
```

Extending the `ContextualDsl` trait enables the creation of DSL actions

```scala
case class ExampleContext()
trait MyDsl extends ContextualDsl[ExampleContext] {
  // actions in here..
}
```

DSL Actions are methods that accepts a context and return Unit

```scala
type Action = Context => Unit
```

#### DSL Actions

The following DSL actions can be used as basic helpers to create other DSL actions

__mapContext__

```scala
mapContext(f: Context => Context)
```

It maps the context to a new context and passes it into the inner action:

```scala
mapContext ( ctx => updateContext(ctx) ) {
  ctx => Unit
}
```

__provide__

```scala
provide[T](value: T)
```

It provides/passes a value into the inner action:

```scala
provide ( "hello" ) { str =>
  ctx => println(str) // hello
}
```

__extract__

```scala
extract[T](f: Context => T)
```

It extracts a value from the context and passes it into the inner action:

```scala
extract ( ctx => ctx.total ) { total =>
  ctx => println(total)
}
```

__onComplete__

```scala
onComplete[T](f: => Future[T])(implicit ec: ExecutionContext)
onComplete[T](f: Context => Future[T])(implicit ec: ExecutionContext)
```

It waits for the completion of a future a passes the future result into the inner action:

```scala
onComplete ( Future { "hello" } ) {
  case Success(str) => ctx => println(str) // hello
  case Failure(ex) => throw ex
}
```

It can also accept as a parameter a function that returns a future from a context:

```scala
onComplete ( ctx => Future { "hello" } ) {
  case Success(str) => ctx => println(str) // hello
  case Failure(ex) => throw ex
}
```

__onSuccess__

```scala
onSuccess[T](f: => Future[T])(implicit ec: ExecutionContext)
onSuccess[T](f: Context => Future[T])(implicit ec: ExecutionContext)
```

It waits for the completion of a future a passes the future result if successful into the inner action:

```scala
onSuccess ( Future { "hello" } ) { str =>
  ctx => println(str) // hello
}
```

It can also accept as a parameter a function that returns a future from a context:

```scala
onSuccess ( ctx => Future { "hello" } ) { str =>
  ctx => println(str) // hello
}
```

__onFailure__

```scala
onFailure[T](f: => Future[T])(implicit ec: ExecutionContext)
onFailure[T](f: Context => Future[T])(implicit ec: ExecutionContext)
```

It waits for the completion of a future a passes the future result if successful into the inner action:

```scala
onFailure ( Future { throw new Exception("Future failure") } ) { ex =>
  ctx => throw ex
}
```

It can also accept as a parameter a function that returns a future from a context:

```scala
onFailure ( ctx => Future { throw new Exception("Future failure") } ) { ex =>
  ctx => throw ex
}
```

__after__

```scala
after[T](delay: FiniteDuration)(implicit ec: ExecutionContext, ac: ActorContext)
```

It exectues the inner action after a given delay:

```scala
after ( 3 seconds ) {
  ctx => Unit
}
```

#### Custom Actions

All the actions above have same result type, which extends the following abstract class:

```scala
abstract class ChainableAction[L]
```

To create a custom action you can use the following 2 helper types:

```scala
type ChainableAction0 = ChainableAction[Unit]
type ChainableAction1[T] = ChainableAction[Tuple1[T]]
```

The first type returns a chainable action, which will call the inner action by passing only the context as an argument, while the second one will pass a custom value of type T that can be used by the inner action.

Let's use `mapContext` and `provide` as an example that return the 2 chainable action types:

```scala
def mapContext(f: Context => Context): ChainableAction0
def provide[T](value: T): ChainableAction1[T]
```

The ChainableAction0 will then have an apply method with the following signature:

```scala
def apply(fn: Action)
```

Where `Action` is a shorthand type for `Context => Unit`.

While for `ChainableAction1[String]` would be:

```scala
def apply(fn: String => Action)
```

To define a custom chainable action we can use the apply method defined on the `ChainableAction` companion object:

```scala
object ChainableAction {
  def apply[T: Tuple](f: (T => Action) => Action): ChainableAction[T]
}
```

or we can define the tapply method part of the abstract class `ChainableAction` directly:

```scala
abstract class ChainableAction[L](implicit val ev: Tuple[L]) {
  def tapply(f: L => Action): Action
}
```

Here's an example of a custom action that returns a `ChainableAction0`, which prints "hello" and then calls the inner action:

```scala
// define it
def printHello: ChainableAction0 = ChainableAction { inner =>
  ctx =>
    println("hello")
    inner(())(ctx)
  }

// then use it
printHello {
  ctx => ()
}
```

Here's an example of a custom action that returns a `ChainableAction1[Int]`, which passes a random integer to the inner action:

```scala
// define it
def randomize: ChainableAction1[Int] = ChainableAction { inner =>
  ctx =>
    val randomInt = scala.util.Random.nextInt
    inner(Tuple1(randomInt))(ctx)
}

// then use it
randomize { randomInt =>
  ctx => println(randomInt)
}
```

Projects using Sclaext
----------------------

- [scalescrape](https://github.com/bfil/scalescrape) - an actor-based web scraping library

License
-------

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2014-2017 Bruno Filippone <http://bfil.io>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    [http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
