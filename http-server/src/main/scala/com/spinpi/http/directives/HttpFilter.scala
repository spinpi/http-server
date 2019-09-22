package com.spinpi.http.directives

import akka.http.scaladsl.server.Directive0

trait HttpFilter {
  def directive: Directive0
}
