name := "http-server"

version := "0.1"

scalaVersion := "2.12.10"
scalacOptions ++= Seq("-deprecation", "-feature")

val httpServerDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "com.typesafe" % "config" % "1.3.2"
)

val graphQLDependencies = Seq(
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-slowlog" % "0.1.8",
  "org.sangria-graphql" %% "sangria-circe" % "1.2.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.21.0",
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  "io.circe" %% "circe-optics" % "0.9.3",
)

val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
)

val httpServer = project
  .in(file("http-server"))
  .settings(
    moduleName := "http-server",
    name := "HttpServer",
    libraryDependencies ++= httpServerDependencies ++ testDependencies
  )

val graphQL = project
  .in(file("graphql"))
    .settings(
      moduleName := "graphql",
      name := "GraphQL",
      libraryDependencies ++=graphQLDependencies ++ testDependencies
    )
    .dependsOn(httpServer)

scalafmtOnCompile := true
