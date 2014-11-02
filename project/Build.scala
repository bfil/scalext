import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild with BFilPlugins {

  val buildVersion = "0.2.0-SNAPSHOT"
    
  lazy val project = BFilProject("scalext", file("."))
  .settings(libraryDependencies ++= Dependencies.all(scalaVersion.value))
}

object Dependencies {
  def all(scalaVersion: String) = Seq(
    scalaVersion match {
      case "2.11.2" => "com.chuusai" %% "shapeless" % "2.0.0"
      case "2.10.4" => "com.chuusai" %% "shapeless" % "1.2.4"
    },
    "com.typesafe.akka" %% "akka-actor" % "2.3.3",
    "org.specs2" %% "specs2" % "2.3.12" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test")
}