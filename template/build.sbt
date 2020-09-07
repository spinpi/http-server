import Versions._
name := "http-server-template"

libraryDependencies ++= Seq(
  "com.github.spullara.mustache.java" % "compiler"        % mustacheCompilerV,
  "commons-io"                        % "commons-io"      % commonIoV,
  "org.scalatest"                     %% "scalatest"      % scalatestV % Test,
  "ch.qos.logback"                    % "logback-classic" % logbackV % Test
)
