package com.spinpi.graphql

import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.unmarshalling.Unmarshaller.NoContentException
import io.circe.Json
import io.circe.parser.parse
import sangria.ast.Document
import sangria.parser.QueryParser

import scala.util.{Failure, Success}

final case class GraphQLRejection(error: Throwable)
    extends RejectionWithOptionalCause {
  override val cause = Some(error)
}

trait GraphQLDirectives {
  type Directive2[T1, T2] = Directive[(T1, T2)]

  def parserDirective(query: Option[String],
                      variables: Option[String]): Directive2[Document, Json] = {
    query.map(QueryParser.parse(_)) match {
      case Some(Success(ast)) =>
        variableParserDirective(variables).flatMap { json =>
          tprovide(Tuple2(ast, json))
        }
      case Some(Failure(error)) =>
        reject(GraphQLRejection(error))

      case None =>
        reject(GraphQLRejection(NoContentException))
    }
  }

  def variableParserDirective(variables: Option[String]): Directive1[Json] = {
    variables.map(parse) match {
      case Some(Left(error)) => reject(GraphQLRejection(error))
      case Some(Right(json)) => provide(json)
      case None              => provide(Json.obj())
    }
  }

  def explicitlyAccepts(mediaType: MediaType): Directive0 =
    headerValuePF {
      case Accept(ranges)
          if ranges.exists(
            range => !range.isWildcard && range.matches(mediaType)
          ) =>
        ranges
    }.flatMap(_ => pass)
}

object GraphQLDirectives extends GraphQLDirectives
