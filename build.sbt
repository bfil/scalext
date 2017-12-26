lazy val root = Project("root", file("."))
  .settings(settings, publishArtifact := false)
  .aggregate(scalext, scalextTestkit, scalextTests)

lazy val scalext = Project("scalext", file("scalext"))
  .settings(settings, libraryDependencies ++= (scalaVersion.value match {
    case "2.10.7" => Seq("com.typesafe.akka" %% "akka-actor" % "2.3.16")
    case _        => Seq("com.typesafe.akka" %% "akka-actor" % "2.5.8")
  }))

lazy val scalextTestkit = Project("scalext-testkit", file("scalext-testkit"))
  .settings(settings)
  .dependsOn(scalext)

lazy val scalextTests = Project("scalext-tests", file("scalext-tests"))
  .settings(settings, publishArtifact := false, libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "3.8.6",
    "org.specs2" %% "specs2-mock" % "3.8.6",
    "org.mockito" % "mockito-all" % "1.10.19",
    "org.hamcrest" % "hamcrest-all" % "1.3"
  ))
  .dependsOn(scalext, scalextTestkit)

lazy val settings = Seq(
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.12", "2.10.7"),
  organization := "io.bfil",
  organizationName := "Bruno Filippone",
  organizationHomepage := Some(url("http://bfil.io")),
  homepage := Some(url("https://github.com/bfil/scalext")),
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
  developers := List(
    Developer("bfil", "Bruno Filippone", "bruno@bfil.io", url("http://bfil.io"))
  ),
  startYear := Some(2014),
  publishTo := Some("Bintray" at s"https://api.bintray.com/maven/bfil/maven/${name.value}"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".bintray-credentials"),
  scmInfo := Some(ScmInfo(
    url(s"https://github.com/bfil/scalext"),
    s"git@github.com:bfil/scalext.git"
  ))
)
