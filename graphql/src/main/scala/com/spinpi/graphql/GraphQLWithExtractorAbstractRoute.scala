package com.spinpi.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.model.MediaTypes.`text/html`
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  Directive1,
  Route,
  StandardRoute,
  UnsupportedRequestContentTypeRejection
}
import com.spinpi.graphql.RequestUnmarshaller._
import com.spinpi.http.routes.{HttpRoute, HttpRouteGroup}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.Json
import io.circe.optics.JsonPath.root
import sangria.ast.Document
import com.spinpi.http.routes._

import scala.concurrent.{ExecutionContext, Future}

trait GraphQLWithExtractorAbstractRoute[ExtractedData]
    extends HttpRouteGroup
    with GraphQLDirectives {
  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val tracingHeader         = "X-Apollo-Tracing"
  val graphqlPlaygroundHtml = "graphql-playground.html"

  def isDisablePlayground: Boolean = false

  def requestExtractor: Directive1[Option[ExtractedData]]

  def executeGraphQL(
      query: Document,
      operationName: Option[String],
      variables: Json,
      tracing: Boolean,
      extractedData: Option[ExtractedData]
  ): Future[Json]

  private def executeAndComplete(
      ast: Document,
      operationName: Option[String],
      json: Json,
      tracing: Option[String],
      extractedData: Option[ExtractedData]
  ): StandardRoute = {
    complete {
      executeGraphQL(ast, operationName, json, tracing.isDefined, extractedData)
        .map(OK -> _)
    }
  }

  private val queryByGet = optionalHeaderValueByName(tracingHeader) { tracing =>
    get {
      parameters(
        "query".as[String],
        "operationName".as[String].optional,
        "variables".as[String].optional
      ) { (query, operationName, variables) ⇒
        parserDirective(Some(query), variables) { (ast, json) =>
          requestExtractor { data =>
            executeAndComplete(ast, operationName, json, tracing, data)
          }
        }
      }
    }
  }

  private val queryByPost = optionalHeaderValueByName(tracingHeader) { tracing ⇒
    post {
      parameters(
        "query".as[String].optional,
        "operationName".as[String].optional,
        "variables".as[String].optional
      ) { (queryParam, operationNameParam, variablesParam) ⇒
        requestExtractor { data =>
          entity(as[Json]) { body ⇒
            val query = queryParam orElse root.query.string.getOption(body)
            val operationName =
              operationNameParam orElse root.operationName.string
                .getOption(body)
            val variablesStr = variablesParam orElse root.variables.string
              .getOption(body)

            parserDirective(query, variablesStr) { (ast, json) =>
              executeAndComplete(ast, operationName, json, tracing, data)
            }
          } ~
            entity(as[Document]) { document ⇒
              variableParserDirective(variablesParam) { json =>
                executeAndComplete(
                  document,
                  operationNameParam,
                  json,
                  tracing,
                  data
                )
              }
            }
        }

      }
    }
  }

  val playGroundRoute: Route = get {
    explicitlyAccepts(`text/html`) {
      if (isDisablePlayground) {
        reject(UnsupportedRequestContentTypeRejection(Set.empty, None))
      } else {
        getFromResource(graphqlPlaygroundHtml)
      }
    }
  }

  override lazy val routes: Seq[HttpRoute] =
    Seq(playGroundRoute, queryByGet, queryByPost)
}
