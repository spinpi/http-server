package com.spinpi.http.routes

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.google.inject.name.Names
import com.google.inject.{Guice, Key}
import com.spinpi.http.directives.{AccessLoggingFilter, ExceptionMapper}
import com.spinpi.http.modules.HttpModule
import com.typesafe.config.Config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.Future

class HelloWorldRoute extends HttpRoute {
  override def route: Route = path("hello") {
    get {
      complete("Hello World!")
    }
  }
}
class HelloRoute      extends HttpRoute {
  override def route: Route = path("hello") {
    post {
      entity(as[String]) { name =>
        complete(s"Hello ${name}!")
      }
    }
  }
}

class RejectionTestRoutes extends HttpRouteGroup {

  object Auth       extends HttpRoute {
    override def route: Route = path("auth") {
      reject(
        AuthenticationFailedRejection(
          CredentialsRejected,
          HttpChallenge("", "")
        )
      )
    }
  }
  object BadRequest extends HttpRoute {
    override def route: Route = path("bad") {
      reject(
        MalformedQueryParamRejection(
          "name",
          "Name must  be a valid ASCII string"
        )
      )
    }
  }

  override val prefix: String         = "rejection"
  override val routes: Seq[HttpRoute] = Seq(
    Auth,
    BadRequest
  )
}
class ExceptionTestRoute extends HttpRoute {
  import akka.http.scaladsl.marshalling.GenericMarshallers._
  override def route: Route = path("exception") {
    get {
      complete(Future.failed[String](new Exception("test-error")))
    }
  }
}

class TestRejectionHandler extends RejectionHandler {
  override def apply(rejections: Seq[Rejection]): Option[Route] = {
    rejections.toList match {
      case Nil           => None
      case single :: Nil => Some(singleRejection(single))
      case multi         => Some(multipleRejection(multi))
    }
  }

  private def singleRejection(rejection: Rejection): Route = {
    rejection match {
      case AuthenticationFailedRejection(_, _) =>
        complete(401, "not authenticated")
      case AuthorizationFailedRejection        => complete(403, "not authorized")
      case _                                   => complete(400, "bad request")
    }
  }

  private def multipleRejection(rejections: List[Rejection]): Route = {
    complete(
      400,
      s"multiple rejections: [${rejections.map(_.getClass.getSimpleName).mkString(",")}]"
    )
  }
}

class TestExceptionMapper extends ExceptionMapper with Directives {
  import akka.http.scaladsl.model.StatusCodes._
  override def handler: PartialFunction[Throwable, Route] = {
    case e: Exception =>
      complete(InternalServerError -> s"Error happened: ${e.getMessage}")
  }
}

class HttpRouterTest
    extends AnyFlatSpec
    with should.Matchers
    with ScalatestRouteTest {

  private val testInjector = Guice.createInjector(HttpModule)
  private val rootConfig   = testInjector.getInstance(
    Key.get(classOf[Config], Names.named("rootConfig"))
  )
  private val httpRouter   = new HttpRouter(testInjector, rootConfig)
  httpRouter.addPreFilter[AccessLoggingFilter]
  httpRouter.add[HelloWorldRoute]
  httpRouter.add[HelloRoute]
  httpRouter.add[RejectionTestRoutes]
  httpRouter.add[ExceptionTestRoute]
  httpRouter.addExceptionMapper[TestExceptionMapper]

  private val route = httpRouter.getHttpHandler(Some(new TestRejectionHandler))

  "Router" should "handle HelloWorld" in {
    Get("/hello") ~> route ~> check {
      responseAs[String] shouldBe "Hello World!"
    }
  }

  "Router" should "handle Hello with name" in {
    Post("/hello", "James") ~> route ~> check {
      responseAs[String] shouldBe "Hello James!"
    }
  }

  "Router" should "handle rejection" in {
    Get("/rejection/auth") ~> route ~> check {
      status.intValue() shouldBe 401
      responseAs[String] shouldBe "not authenticated"
    }

    Get("/rejection/bad") ~> route ~> check {
      status.intValue() shouldBe 400
      responseAs[String] shouldBe "bad request"
    }
  }

  "Router" should "handle exception" in {
    Get("/exception") ~> route ~> check {
      status.intValue() shouldBe 500
      responseAs[String] shouldBe "Error happened: test-error"
    }
  }
}
