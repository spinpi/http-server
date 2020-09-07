package com.spinpi.graphql.schema

import sangria.schema.Field

class GraphQLMutations[Ctx] extends GraphQLFields[Ctx] {
  def withMutations(mutations: List[Field[Ctx, Unit]]): this.type = {
    withFields(mutations)
    this
  }
}
