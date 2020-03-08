package com.spinpi.http.routes

import akka.http.scaladsl.server.Route

trait HttpRouteGroup extends HttpRoute {
  val prefix: String
  val routes: Seq[HttpRoute]

  override lazy val route: Route = pathPrefix(prefix) {
    val httpRoutes = routes.map(_.route)
    concat(httpRoutes: _*)
  }
}
