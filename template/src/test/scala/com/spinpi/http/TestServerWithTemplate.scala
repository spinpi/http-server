package com.spinpi.http

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.{
  AuthenticationFailedRejection,
  Directives,
  Route
}
import com.google.inject.Inject
import com.spinpi.http.directives.{AccessLoggingFilter, ExceptionMapper}
import com.spinpi.http.routes.HttpRoute
import com.spinpi.template.model.MustacheResponse
import com.spinpi.template.modules.MustacheModule
import com.spinpi.template.mustache.{MustacheService, MustacheSupport}

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

case class TestMustacheRoute @Inject() (mustacheService: MustacheService)
    extends HttpRoute
    with MustacheSupport {

  override def route: Route = {
    path("helloworld") {
      complete(
        MustacheResponse(
          Map("title" -> "Helloworld", "desc" -> "Hello", "keywords" -> "123"),
          "helloworld.mustache"
        )
      )
    }
  }
}

object TestServerWithTemplate extends HttpServer with App {

  registerModules(MustacheModule)

  router
    .addPreFilter[AccessLoggingFilter]
    .addExceptionMapper[TestExceptionMapper]
    .add[PingRoute]
    .add[RejectedRoute]
    .add[ExceptionRoute]
    .add[TestMustacheRoute]

  startHttpServer()

}
