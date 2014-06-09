import sbt._
import Keys._

object ScalextBuild extends Build {

  val appVersion = "0.1.0-SNAPSHOT"

  lazy val Scalext = Project(
    id = "scalext",
    base = file("."),
    settings = Defaults.defaultSettings ++
      buildSettings ++
      compilersSettings ++
      Publish.settings)

  lazy val buildSettings = Seq(
    name := "scalext",
    organization := "com.bfil",
    version := appVersion,
    scalaVersion := "2.10.4",
    crossPaths := false,
    organizationName := "Bruno Filippone",
    organizationHomepage := Some(url("http://www.b-fil.com")),
    libraryDependencies ++= Dependencies.all,
    resolvers ++= Resolvers.all)

  lazy val compilersSettings = Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"))
}

object Dependencies {
  val all = Seq(
    "com.chuusai" %% "shapeless" % "1.2.4",
    "com.typesafe.akka" %% "akka-actor" % "2.3.1",
    "org.mockito" % "mockito-all" % "1.9.5" % "test",
    "org.scalatest" %% "scalatest" % "2.0" % "test")
}

object Resolvers {
  val all = Seq(
    "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/")
}

object Publish {
  def repository: Project.Initialize[Option[sbt.Resolver]] = version { (version: String) =>
    val s3Bucket = "s3://bfil-mvn-repo.s3-eu-west-1.amazonaws.com/"
    if (version.trim.endsWith("SNAPSHOT")) Some("BFil S3 Repo Snapshots" at s3Bucket + "snapshots")
    else Some("BFil S3 Repo Releases" at s3Bucket + "releases")
  }

  lazy val settings = Seq(
    publishMavenStyle := true,
    publishTo <<= repository,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("http://www.b-fil.com")),
    pomExtra := (
      <scm>
        <url>git://github.com/bfil/scalext.git</url>
        <connection>scm:git://github.com/bfil/scalext.git</connection>
      </scm>
      <developers>
        <developer>
          <id>bfil</id>
          <name>Bruno Filippone</name>
          <url>http://www.b-fil.com</url>
        </developer>
      </developers>))
}