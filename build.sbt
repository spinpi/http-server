organization := "com.spinpi"
organizationName := "spinpi"
organizationHomepage := Some(url("https://spinpi.com"))
name := "http-server"
description := "A HTTP server based on Akka HTTP"
homepage := Some(url("https://github.com/spinpi/http-server"))

lazy val scala212               = "2.12.10"
lazy val scala211               = "2.11.12"
lazy val supportedScalaVersions = List(scala212, scala211)

scalaVersion := scala212
crossScalaVersions := supportedScalaVersions
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka"                 %% "akka-actor"          % "2.5.12",
  "com.typesafe.akka"                 %% "akka-stream"         % "2.5.12",
  "com.typesafe.akka"                 %% "akka-http"           % "10.1.11",
  "com.typesafe.scala-logging"        %% "scala-logging"       % "3.9.2",
  "net.codingwell"                    %% "scala-guice"         % "4.2.6",
  "com.typesafe"                      % "config"               % "1.3.2",
  "org.sangria-graphql"               %% "sangria"             % "1.4.2",
  "org.sangria-graphql"               %% "sangria-slowlog"     % "0.1.8",
  "org.sangria-graphql"               %% "sangria-circe"       % "1.3.0",
  "de.heikoseeberger"                 %% "akka-http-circe"     % "1.30.0",
  "io.circe"                          %% "circe-core"          % "0.12.3",
  "io.circe"                          %% "circe-parser"        % "0.12.3",
  "io.circe"                          %% "circe-optics"        % "0.12.0",
  "com.github.spullara.mustache.java" % "compiler"             % "0.9.6",
  "commons-io"                        % "commons-io"           % "2.6",
  "org.scalatest"                     %% "scalatest"           % "3.0.5" % Test,
  "ch.qos.logback"                    % "logback-classic"      % "1.2.3" % Test
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/spinpi/http-server"),
    "scm:git@github.com:spinpi/http-server.git"
  )
)

developers := List(
  Developer(
    id = "neitomic",
    name = "Tien Nguyen",
    email = "neitomic@gmail.com",
    url = url("https://neitomic.github.io/")
  )
)

licenses := Seq(
  "MIT License" -> url(
    "http://www.opensource.org/licenses/mit-license.html"
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true
pomIncludeRepository in ThisBuild := { _ =>
  false
}

scalafmtOnCompile := true
isSnapshot := true
