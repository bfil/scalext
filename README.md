Scalext
=======

A scala library that enable the creation of contextual DSLs, inspired by the routing module of [Spray](http://spray.io/).

It uses part of the internal code of [spray-routing](https://github.com/spray/spray/tree/master/spray-routing/src/main/scala/spray/routing) and its directives, but enables developers to use their own contexts and create their own DSL.

Set up the dependencies
-----------------------

Scalext is available at my [S3 Repository](http://shrub.appspot.com/bfil-mvn-repo), and it is cross compiled and published for both Scala 2.10 and 2.11.

Using SBT, add the following plugin:

```scala
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.2.0")
```

Add the following dependency to your SBT build file:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "scalext" % "0.1.0"
)
```

Don't forget to add the following resolver:

```scala
resolvers += "BFil S3 Repo Releases" at "s3://bfil-mvn-repo.s3-eu-west-1.amazonaws.com/releases"
```

### Using snapshots

If you need a snapshot dependency:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "scalext" % "0.2.0-SNAPSHOT"
)

resolvers += "BFil S3 Repo Snapshots" at "s3://bfil-mvn-repo.s3-eu-west-1.amazonaws.com/snapshots"
```

Usage
-----

To build your own DSL all you need to do is:

 1. define your custom context object
 2. define the DSL actions

### A basic example

Let's use a simple calculator DSL as an example.

The calculator DSL context will be a simple class that stores a promise object (which will resolve into a future result at some point) and the current calculation value.

The _ArihmeticContext_ object can be a simple case class:

```scala
case class ArithmeticContext(resultPromise: Promise[Double], value: Double)
```

The DSL actions will manipulate this context, we can put the actions in a _CalculatorDsl_ trait:

```scala
trait CalculatorDsl extends ContextualDsl[ArithmeticContext] {
  // actions in here..
}
```

The first action triggering the DSL logic needs to accept an _Action_ as a parameter.

In this example we create an initial action _startWith_ that accepts an initial value, it creates the _ArithmeticContext_ storing the initial value and a promise, and it passes the newly created context into the action specified.

```scala
def startWith(initialValue: Long)(action: Action) = {
  val p = Promise[Double]
  action(ArithmeticContext(p, initialValue)) 
  p.future
}
```

The above method returns the future associated with the promise, so that we can resolve the promise at a later point when we want to return a result.

Let's define some basic actions for the DSL (_add_, _subtract_, _multiplyBy_, and _divideBy_), the actions use the _mapContext_ helper method of the _ContextualDsl_ trait, which modifies the immutable context and passes it through to the inner action.

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

We called our final action _returnResult_, and we defined it using the apply method of the _ActionResult_ object, which makes the code more readable, but it could just be a simple method with the signature _Context => Unit_.

Finally using our _CalculatorDsl_ in our code will look like this:

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

TODO 

License
-------

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2014 Bruno Filippone <http://b-fil.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    [http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
