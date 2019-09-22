package com.spinpi.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RejectionHandler
import akka.stream.ActorMaterializer
import com.spinpi.http.inject.InjectApp
import com.spinpi.http.modules.HttpModule
import com.spinpi.http.routes.HttpRouter
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.InjectorExtensions._

trait HttpServer extends InjectApp with LazyLogging {

  protected lazy val httpPort: Int = 8080

  protected lazy val router: HttpRouter = injector.instance[HttpRouter]

  protected lazy val overrideRejectionHandler: RejectionHandler = null

  registerModules(HttpModule)

  def startHttpServer(): Unit = {

    implicit val ec: ActorSystem = injector.instance[ActorSystem]
    implicit val materializer: ActorMaterializer =
      injector.instance[ActorMaterializer]

    val route = router.getHttpHandler(Option(overrideRejectionHandler))
    logger.info(s"Starting sever at 0.0.0.0:$httpPort...")
    Http().bindAndHandle(route, "0.0.0.0", httpPort)
  }
}
