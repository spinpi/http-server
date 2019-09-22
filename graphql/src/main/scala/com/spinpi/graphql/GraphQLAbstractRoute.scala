package com.spinpi.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.model.MediaTypes.`text/html`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
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
import akka.http.scaladsl.model.StatusCodes._

import scala.concurrent.{ExecutionContext, Future}

trait GraphQLAbstractRoute extends HttpRouteGroup with GraphQLDirectives {
  implicit val actorSystem: ActorSystem
  implicit val executionContext: ExecutionContext

  val tracingHeader = "X-Apollo-Tracing"
  val graphqlPlaygroundHtml = "graphql-playground.html"

  def isDisablePlayground: Boolean = false

  def executeGraphQL(query: Document,
                     operationName: Option[String],
                     variables: Json,
                     tracing: Boolean): Future[Json]

  private def executeAndComplete(ast: Document,
                                 operationName: Option[String],
                                 json: Json,
                                 tracing: Option[String]): StandardRoute = {
    complete {
      executeGraphQL(ast, operationName, json, tracing.isDefined)
        .map(OK -> _)
    }
  }

  private val queryByGet = optionalHeaderValueByName(tracingHeader) { tracing =>
    get {
      parameter('query, 'operationName.?, 'variables.?) {
        (query, operationName, variables) ⇒
          parserDirective(Some(query), variables) { (ast, json) =>
            executeAndComplete(ast, operationName, json, tracing)
          }
      }
    }
  }

  private val queryByPost = optionalHeaderValueByName(tracingHeader) { tracing ⇒
    post {
      parameters('query.?, 'operationName.?, 'variables.?) {
        (queryParam, operationNameParam, variablesParam) ⇒
          entity(as[Json]) { body ⇒
            val query = queryParam orElse root.query.string.getOption(body)
            val operationName = operationNameParam orElse root.operationName.string
              .getOption(body)
            val variablesStr = variablesParam orElse root.variables.string
              .getOption(body)

            parserDirective(query, variablesStr) { (ast, json) =>
              complete {
                executeGraphQL(ast, operationName, json, tracing.isDefined)
              }
            }
          } ~
            entity(as[Document]) { document ⇒
              variableParserDirective(variablesParam) { json =>
                complete {
                  executeGraphQL(
                    document,
                    operationNameParam,
                    json,
                    tracing.isDefined
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
        reject(UnsupportedRequestContentTypeRejection(Set.empty))
      } else {
        getFromResource(graphqlPlaygroundHtml)
      }
    }
  }

  override lazy val routes: Seq[HttpRoute] =
    Seq(playGroundRoute, queryByGet, queryByPost)
}
