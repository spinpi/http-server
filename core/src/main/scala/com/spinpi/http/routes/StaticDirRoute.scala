package com.spinpi.http.routes
import akka.http.scaladsl.server.Route

class StaticDirRoute(
    private val path: String,
    private val dir: String,
    private val index: String
) extends HttpRoute {

  override def route: Route = pathPrefix(path) {
    pathEndOrSingleSlash {
      getFromFile(s"$dir/$index")
    } ~
      getFromDirectory(dir)
  }
}

object StaticDirRoute {
  def apply(dir: String): StaticDirRoute = {
    apply(dir, "index.html")
  }
  def apply(dir: String, index: String): StaticDirRoute = {
    apply("", dir, index)
  }
  def apply(pathPrefix: String, dir: String, index: String): StaticDirRoute = {
    new StaticDirRoute(pathPrefix, dir, index)
  }
}
