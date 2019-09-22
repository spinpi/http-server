package com.spinpi.http.directives

import akka.http.scaladsl.server.Route

trait ExceptionMapper {
  def handler: PartialFunction[Throwable, Route]
}

object ExceptionMapper {
  def apply(pf: PartialFunction[Throwable, Route]): ExceptionMapper = {
    new ExceptionMapper {
      override val handler: PartialFunction[Throwable, Route] = pf
    }
  }
}
