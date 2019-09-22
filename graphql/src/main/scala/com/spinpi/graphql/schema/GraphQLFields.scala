package com.spinpi.graphql.schema

import sangria.schema.Field

import scala.collection.mutable.ArrayBuffer

trait GraphQLFields[Ctx] {
  private val fields = ArrayBuffer[Field[Ctx, Unit]]()

  protected def withFields(fieldsToAdd: List[Field[Ctx, Unit]]): Unit = {
    fields ++= fieldsToAdd
  }

  def getFields: List[Field[Ctx, Unit]] = fields.toList
}