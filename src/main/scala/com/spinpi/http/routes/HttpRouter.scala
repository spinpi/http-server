package com.spinpi.http.routes

import akka.http.scaladsl.settings.RoutingSettings
import akka.http.scaladsl.server.{Directive, ExceptionHandler, RejectionHandler}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import com.google.inject.name.Named
import com.spinpi.http.directives.{
  ExceptionMapper,
  HttpFilter,
  HttpRejectHandler
}
import com.google.inject.{Inject, Injector}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.InjectorExtensions._

import scala.collection.mutable.ArrayBuffer

class HttpRouter @Inject() (
    injector: Injector,
    @Named("rootConfig") config: Config
) extends LazyLogging {

  private[http] val routes            = ArrayBuffer[HttpRoute]()
  private[http] val filters           = ArrayBuffer[HttpFilter]()
  private[http] val preFilters        = ArrayBuffer[HttpFilter]()
  private[http] val exceptionMappers  = ArrayBuffer[ExceptionMapper]()
  private[http] val rejectionHandlers = ArrayBuffer[HttpRejectHandler]()

  def getHttpHandler(
      overrideRejectionHandler: Option[RejectionHandler]
  ): server.Route = {
    val rootRoute = concat(routes.map(_.route): _*)
    val allPreFilters = preFilters.foldLeft(Directive.Empty) {
      (directive, filter) => directive & filter.directive
    }
    val allFilters = filters.foldLeft(Directive.Empty) {
      (finalDirective, filter) => finalDirective & filter.directive
    }

    val exceptionHandler = exceptionMappers.toList match {
      case Nil     => ExceptionHandler.default(RoutingSettings(config))
      case mappers => mappers.map(_.handler).reduceLeft(_ orElse _)
    }

    val rejectionHandler = getRejectionHandler(overrideRejectionHandler)

    allPreFilters {
      handleExceptions(exceptionHandler) {
        handleRejections(rejectionHandler) {
          allFilters {
            rootRoute
          }
        }
      }
    }
  }

  def add[R <: HttpRoute: Manifest]: HttpRouter = {
    val route = injector.instance[R]
    addRoute(route)
  }

  def addFilter[F <: HttpFilter: Manifest]: HttpRouter = {
    val filter = injector.instance[F]
    addFilter(filter, preFilter = false)
  }

  def addPreFilter[F <: HttpFilter: Manifest]: HttpRouter = {
    val filter = injector.instance[F]
    addFilter(filter, preFilter = true)
  }

  def addExceptionMapper[EM <: ExceptionMapper: Manifest]: HttpRouter = {
    val em = injector.instance[EM]
    addExceptionMapper(em)
  }

  def addExceptionMapper(em: ExceptionMapper): HttpRouter = {
    exceptionMappers += em
    this
  }

  def addRoute[R <: HttpRoute](route: R): HttpRouter = {
    logger.info(s"Adding route: ${route.getClass.getName}")
    routes += route
    this
  }

  private def addFilter[F <: HttpFilter](
      filter: F,
      preFilter: Boolean
  ): HttpRouter = {
    if (preFilter) preFilters += filter
    else filters += filter
    this
  }

  private def getRejectionHandler(
      custom: Option[RejectionHandler]
  ): RejectionHandler = {
    custom
      .orElse {
        rejectionHandlers.toList match {
          case Nil => None
          case handlers =>
            Some(
              handlers
                .foldLeft(RejectionHandler.newBuilder()) { (builder, handler) =>
                  builder.handle(handler.handler)
                }
                .result()
            )
        }
      }
      .getOrElse(RejectionHandler.default)
  }
}
