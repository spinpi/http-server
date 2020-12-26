import Versions._
import xerial.sbt.Sonatype._

publishArtifact := false

lazy val commonSettings = Seq(
  organization := "com.spinpi",
  organizationName := "SpinPI",
  organizationHomepage := Some(url("https://spinpi.com")),
  description := "A HTTP server based on Akka HTTP",
  scalaVersion := scala213,
  crossScalaVersions := supportedScalaVersions,
  javacOptions ++= Seq("-encoding", "utf-8"),
  scalacOptions ++= Seq("-encoding", "utf-8"),
  scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-feature"),
  scalafmtOnCompile := true
)

lazy val publishSettings = Seq(
  publishTo := sonatypePublishToBundle.value,
  sonatypeProfileName := "com.spinpi",
  publishMavenStyle := true,
  licenses := Seq(
    "MIT License" -> url(
      "http://www.opensource.org/licenses/mit-license.html"
    )
  ),
  sonatypeProjectHosting := Some(
    GitHubHosting("spinpi", "http-server", "neitomic@gmail.com")
  ),
  homepage := Some(url("https://github.com/spinpi/http-server")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/spinpi/http-server"),
      "scm:git@github.com:spinpi/http-server.git"
    )
  ),
  developers := List(
    Developer(
      id = "neitomic",
      name = "Tien Nguyen",
      email = "neitomic@gmail.com",
      url = url("https://neitomic.github.io/")
    )
  )
)

val core = project
  .in(file("core"))
  .settings(commonSettings ++ publishSettings)

val graphql = project
  .in(file("graphql"))
  .settings(commonSettings ++ publishSettings)
  .dependsOn(core)

val template = project
  .in(file("template"))
  .settings(commonSettings ++ publishSettings)
  .dependsOn(core)


import ReleaseTransformations._

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
