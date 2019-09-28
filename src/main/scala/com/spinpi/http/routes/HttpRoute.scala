package com.spinpi.http.routes

import akka.http.scaladsl.server.Route

trait HttpRoute {
  def route: Route
}
