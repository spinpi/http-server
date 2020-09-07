package com.spinpi.http.routes

import akka.http.scaladsl.server.{Directives, Route}

trait HttpRoute extends Directives {
  def route: Route
}
