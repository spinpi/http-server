package com.spinpi.http.directives
import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging

class AccessLoggingFilter extends HttpFilter with LazyLogging {

  override def directive: Directive0 =
    extractRequestContext.flatMap { ctx =>
      extractClientIP.flatMap { remoteAddress =>
        val start = System.currentTimeMillis()
        val ip =
          remoteAddress.toOption.map(_.getHostAddress).getOrElse("unknown")
        mapResponse { resp =>
          val elapse = System.currentTimeMillis() - start
          logger.info(
            s"[$ip] [${resp.status
              .intValue()}] ${ctx.request.method.name} ${ctx.request.uri.path} took: ${elapse}ms"
          )
          resp
        }
      }
    }
}
