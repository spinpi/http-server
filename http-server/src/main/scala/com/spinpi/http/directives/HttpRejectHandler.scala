package com.spinpi.http.directives

import akka.http.scaladsl.server.{Rejection, Route}

trait HttpRejectHandler {
  def handler: PartialFunction[Rejection, Route]
}
