package com.spinpi.http.routes

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives

trait HttpRoute extends Directives {
  def route: Route
}
