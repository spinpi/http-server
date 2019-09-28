package com.spinpi.http

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import com.spinpi.http.routes.HttpRoute
import akka.http.scaladsl.server.Directives._
import com.spinpi.http.directives.{AccessLoggingFilter, ExceptionMapper}

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

class TestExceptioMapper extends ExceptionMapper {
  import akka.http.scaladsl.model.StatusCodes._
  override def handler: PartialFunction[Throwable, Route] = {
    case e: Exception =>
      complete(InternalServerError -> s"Error happened: ${e.getMessage}")
  }
}

object TestServer extends HttpServer with App {

  router
    .addPreFilter[AccessLoggingFilter]
    .addExceptionMapper[TestExceptioMapper]
    .add[PingRoute]
    .add[RejectedRoute]
    .add[ExceptionRoute]

  startHttpServer()

}
