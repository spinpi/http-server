package com.spinpi.http

import akka.http.scaladsl.server
import akka.http.scaladsl.server.Route

import scala.language.implicitConversions

package object routes {

  implicit def toHttpRoute(_route: server.Route): HttpRoute = {
    new HttpRoute {
      override def route: Route = _route
    }
  }
}
