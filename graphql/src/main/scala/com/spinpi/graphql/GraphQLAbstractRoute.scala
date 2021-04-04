package com.spinpi.graphql

import akka.http.scaladsl.server.Directive1
import io.circe.Json
import sangria.ast.Document
import sangria.execution.{Executor, Middleware}
import sangria.marshalling.circe._
import sangria.schema.Schema
import sangria.slowlog.SlowLog

import scala.concurrent.Future

trait GraphQLAbstractRoute[Ctx]
    extends GraphQLWithExtractorAbstractRoute[Unit] {

  val context: Ctx
  val schema: Schema[Ctx, Unit]
  val middlewares: List[Middleware[Ctx]] = List.empty[Middleware[Ctx]]

  override def requestExtractor: Directive1[Option[Unit]] = {
    provide(None)
  }

  def executeGraphQL(
      query: Document,
      operationName: Option[String],
      variables: Json,
      tracing: Boolean,
      extractedData: Option[Unit]
  ): Future[Json] = {
    executeGraphQL(
      query,
      operationName,
      variables,
      tracing
    )
  }

  def executeGraphQL(
      query: Document,
      operationName: Option[String],
      variables: Json,
      tracing: Boolean
  ): Future[Json] = {
    Executor.execute(
      schema,
      query,
      context,
      variables = variables,
      operationName = operationName,
      middleware =
        if (tracing) SlowLog.apolloTracing :: middlewares else middlewares
    )
  }
}
