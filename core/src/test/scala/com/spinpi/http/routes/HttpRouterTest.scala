package com.spinpi.http.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.google.inject.name.Names
import com.google.inject.{Guice, Key}
import com.spinpi.http.modules.HttpModule
import com.typesafe.config.Config
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

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

class HttpRouterTest
    extends AnyFlatSpec
    with should.Matchers
    with ScalatestRouteTest {

  private val testInjector = Guice.createInjector(HttpModule)
  private val rootConfig   = testInjector.getInstance(
    Key.get(classOf[Config], Names.named("rootConfig"))
  )
  private val httpRouter   = new HttpRouter(testInjector, rootConfig)
  httpRouter.add[HelloWorldRoute]
  httpRouter.add[HelloRoute]
  private val route        = httpRouter.getHttpHandler(None)

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
}
