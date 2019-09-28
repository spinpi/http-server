package com.spinpi.graphql

import akka.actor.ActorSystem
import com.google.inject.{Inject, Provides, Singleton}
import com.spinpi.graphql.schema.{GraphQLMutations, GraphQLQueries}
import com.spinpi.http.HttpServer
import com.spinpi.http.directives.AccessLoggingFilter
import io.circe.Json
import sangria.ast.Document
import sangria.execution.Executor
import sangria.marshalling.circe._
import sangria.schema.Schema
import sangria.slowlog.SlowLog
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}

case class Character(id: String, name: Option[String], friends: Seq[String])

case class GraphQLContext() {

  val characters = List[Character](
    Character(
      id = "1000",
      name = Some("Luke Skywalker"),
      friends = List("1002", "1003")
    ),
    Character(id = "1001", name = Some("Darth Vader"), friends = List("1004")),
    Character(
      id = "1002",
      name = Some("Han Solo"),
      friends = List("1000", "1003")
    ),
    Character(
      id = "1003",
      name = Some("Leia Organa"),
      friends = List("1000", "1002")
    ),
    Character(
      id = "1004",
      name = Some("Wilhuff Tarkin"),
      friends = List("1001")
    )
  )

}

class GraphQLRoute @Inject()(
    context: GraphQLContext,
    schema: Schema[GraphQLContext, Unit],
    implicit val actorSystem: ActorSystem,
    val executionContext: ExecutionContext
) extends GraphQLAbstractRoute {

  implicit val dispatcher = actorSystem.dispatcher

  override def executeGraphQL(
      query: Document,
      operationName: Option[String],
      variables: Json,
      tracing: Boolean
  ): Future[Json] = {
    Executor
      .execute(
        schema,
        query,
        context,
        variables = variables,
        operationName = operationName,
        middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil
      )
  }

  override val prefix: String = "graphql"
}

object CharacterSchema {

  val Character: ObjectType[GraphQLContext, Character] =
    ObjectType(
      "Character",
      "A character in the Star Wars Trilogy",
      () ⇒
        fields[GraphQLContext, Character](
          Field(
            "id",
            StringType,
            Some("The id of the character."),
            resolve = _.value.id
          ),
          Field(
            "name",
            OptionType(StringType),
            Some("The name of the character."),
            resolve = _.value.name
          ),
          Field(
            "friends",
            ListType(Character),
            Some(
              "The friends of the character, or an empty list if they have none."
            ),
            resolve = c =>
              c.ctx.characters.filter(
                character => c.value.friends.toSet.contains(character.id)
              )
          )
        )
    )

  val schemaFields = fields[GraphQLContext, Unit](
    Field(
      "characters",
      ListType(Character),
      arguments = Nil,
      resolve = c ⇒ c.ctx.characters
    )
  )

}

object GraphQLModule extends GraphQLAbstractModule {
  @Provides
  @Singleton
  def providesSchema = {

    val queries = new GraphQLQueries[GraphQLContext]
      .withQueries(CharacterSchema.schemaFields)

    val mutations = new GraphQLMutations[GraphQLContext]

    createSchema[GraphQLContext](queries, mutations)
  }

  @Provides
  def providesEC(system: ActorSystem): ExecutionContext = system.dispatcher
}

object GraphQLServer extends App with HttpServer {
  val actorSystem = ActorSystem("Test")
  registerModules(GraphQLModule)

  router
    .addPreFilter[AccessLoggingFilter]
    .add[GraphQLRoute]

  startHttpServer()
}
