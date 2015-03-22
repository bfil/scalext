import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild {

  val buildVersion = "0.3.0-SNAPSHOT"

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
    .settings(Publish.noPublish: _*)
}

object Dependencies {
  def core(scalaVersion: String) = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.9")

  def tests(scalaVersion: String) = Seq(
    "org.specs2" %% "specs2-core" % "2.4.17",
    "org.specs2" %% "specs2-mock" % "2.4.17",
    "org.mockito" % "mockito-all" % "1.10.19",
    "org.hamcrest" % "hamcrest-all" % "1.3")

  val none = Seq.empty
}