import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild {

  lazy val root = BFilRootProject("root", file("."))
    .aggregate(scalext, scalextTestkit, scalextTests)

  lazy val scalext = BFilProject("scalext", file("scalext"))
    .settings(libraryDependencies ++= Dependencies.core(scalaVersion.value))

  lazy val scalextTestkit = BFilProject("scalext-testkit", file("scalext-testkit"))
    .settings(libraryDependencies ++= Dependencies.none)
    .dependsOn(scalext)

  lazy val scalextTests = BFilProject("scalext-tests", file("scalext-tests"))
    .settings(libraryDependencies ++= Dependencies.tests(scalaVersion.value))
    .dependsOn(scalext, scalextTestkit)
    .settings(Publish.noPublish)
}

object Dependencies {

  def core(scalaVersion: String) = Seq(
    if(scalaVersion == "2.10.6") "com.typesafe.akka" %% "akka-actor" % "2.3.15"
    else "com.typesafe.akka" %% "akka-actor" % "2.4.16"
  )

  def tests(scalaVersion: String) = Seq(
    "org.specs2" %% "specs2-core" % "3.8.6",
    "org.specs2" %% "specs2-mock" % "3.8.6",
    "org.mockito" % "mockito-all" % "1.10.19",
    "org.hamcrest" % "hamcrest-all" % "1.3"
  )

  val none = Seq.empty
}
