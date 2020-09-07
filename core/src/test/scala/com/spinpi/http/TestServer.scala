package com.spinpi.http

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.{
  AuthenticationFailedRejection,
  Directives,
  Route
}
import com.spinpi.http.directives.{AccessLoggingFilter, ExceptionMapper}
import com.spinpi.http.routes.HttpRoute

import scala.concurrent.Future

class PingRoute extends HttpRoute {
  override def route: Route = path("ping") {
    complete("pong")
  }
}

class RejectedRoute extends HttpRoute {
  override def route: Route = path("rejected") {
    reject(
      AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", ""))
    )
  }
}

class ExceptionRoute extends HttpRoute {
  import akka.http.scaladsl.marshalling.GenericMarshallers._
  override val route: Route = path("exception") {
    complete(Future.failed[String](new Exception("Error")))
  }
}

class TestExceptionMapper extends ExceptionMapper with Directives {
  import akka.http.scaladsl.model.StatusCodes._
  override def handler: PartialFunction[Throwable, Route] = {
    case e: Exception =>
      complete(InternalServerError -> s"Error happened: ${e.getMessage}")
  }
}

object TestServer extends HttpServer with App {

  router
    .addPreFilter[AccessLoggingFilter]
    .addExceptionMapper[TestExceptionMapper]
    .add[PingRoute]
    .add[RejectedRoute]
    .add[ExceptionRoute]

  startHttpServer()

}
