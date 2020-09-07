package com.spinpi.graphql

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.provide
import io.circe.Json
import sangria.ast.Document

import scala.concurrent.Future

trait GraphQLAbstractRoute extends GraphQLWithExtractorAbstractRoute[Unit] {

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
  ): Future[Json]
}
