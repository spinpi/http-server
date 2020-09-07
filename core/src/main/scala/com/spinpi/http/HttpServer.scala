package com.spinpi.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RejectionHandler
import akka.stream.Materializer
import com.spinpi.http.modules.HttpModule
import com.spinpi.http.routes.HttpRouter
import com.spinpi.inject.InjectApp
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector

trait HttpServer extends InjectApp with LazyLogging {

  protected lazy val interface: String = "0.0.0.0"
  protected lazy val httpPort: Int     = 8080

  protected lazy val router: HttpRouter = injector.instance[HttpRouter]

  protected lazy val overrideRejectionHandler: RejectionHandler = null

  registerModules(HttpModule)

  def startHttpServer(): Unit = {

    implicit val ec: ActorSystem            = injector.instance[ActorSystem]
    implicit val materializer: Materializer = injector.instance[Materializer]

    val route = router.getHttpHandler(Option(overrideRejectionHandler))
    logger.info(s"Starting sever at $interface:$httpPort...")
    Http().bindAndHandle(route, interface, httpPort)
  }
}
