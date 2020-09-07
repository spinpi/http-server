import Versions._

lazy val commonSettings = Seq(
  organization := "com.spinpi",
  organizationName := "SpinPI",
  organizationHomepage := Some(url("https://spinpi.com")),
  description := "A HTTP server based on Akka HTTP",
  homepage := Some(url("https://github.com/spinpi/http-server")),
  scalaVersion := scala213,
  crossScalaVersions := supportedScalaVersions,
  javacOptions ++= Seq("-encoding", "utf-8"),
  scalacOptions ++= Seq("-encoding", "utf-8"),
  scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-feature"),
  scalafmtOnCompile := true
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
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
  ),
  licenses := Seq(
    "MIT License" -> url(
      "http://www.opensource.org/licenses/mit-license.html"
    )
  )
)

val core = project
  .in(file("core"))
  .settings(commonSettings)

val graphql = project.in(file("graphql"))
  .settings(commonSettings)
  .dependsOn(core)

val template = project
  .in(file("template"))
  .settings(commonSettings)
  .dependsOn(core)
