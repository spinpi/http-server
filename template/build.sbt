import Versions._
name := "http-server-template"

libraryDependencies ++= Seq(
  "com.github.spullara.mustache.java" % "compiler"               % mustacheCompilerV,
  "commons-io"                        % "commons-io"             % commonIoV,
//  "javax.activation"                  % "javax.activation-api" % activationV,
  "jakarta.activation"                % "jakarta.activation-api" % jakartaV,
  "org.eclipse.angus"                 % "angus-activation"       % "1.0.0"    % "runtime",
  "org.scalatest"                    %% "scalatest"              % scalatestV % Test,
  "ch.qos.logback"                    % "logback-classic"        % logbackV   % Test
)
