import Versions._

name := "http-server-graphql"

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria"         % sangriaV,
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-circe"   % "1.3.0",
  "de.heikoseeberger"   %% "akka-http-circe" % "1.34.0",
  "io.circe"            %% "circe-core"      % circeV,
  "io.circe"            %% "circe-parser"    % circeV,
  "io.circe"            %% "circe-optics"    % circeV,
  "org.scalatest"       %% "scalatest"       % scalatestV % Test,
  "ch.qos.logback"       % "logback-classic" % logbackV   % Test
)
