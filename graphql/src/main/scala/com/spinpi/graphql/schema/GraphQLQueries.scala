package com.spinpi.graphql.schema

import sangria.schema.Field

class GraphQLQueries[Ctx] extends GraphQLFields[Ctx] {
  def withQueries(queries: List[Field[Ctx, Unit]]): this.type = {
    withFields(queries)
    this
  }
}
