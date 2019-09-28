package com.spinpi.graphql

import com.spinpi.graphql.schema.{GraphQLMutations, GraphQLQueries}
import net.codingwell.scalaguice.ScalaModule
import sangria.schema.{ObjectType, Schema}

trait GraphQLAbstractModule extends ScalaModule {

  def createSchema[Ctx](
      queries: GraphQLQueries[Ctx],
      mutations: GraphQLMutations[Ctx]
  ): Schema[Ctx, Unit] = {

    val queryObject = ObjectType("Query", queries.getFields)

    val mutationObject = mutations.getFields match {
      case Nil    => None
      case fields => Some(ObjectType("Mutable", fields))
    }

    Schema[Ctx, Unit](queryObject, mutationObject)
  }
}
