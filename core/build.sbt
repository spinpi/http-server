import Versions._
name := "http-server-core"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor"          % akkaV,
  "com.typesafe.akka"          %% "akka-stream"         % akkaV,
  "com.typesafe.akka"          %% "akka-http"           % akkaHttpV,
  "com.typesafe.scala-logging" %% "scala-logging"       % scalaLoggingV,
  "net.codingwell"             %% "scala-guice"         % scalaGuiceV,
  "com.typesafe"               % "config"               % typesafeConfigV,
  "org.scalatest"              %% "scalatest"           % scalatestV % Test,
  "ch.qos.logback"             % "logback-classic"      % logbackV % Test
)
