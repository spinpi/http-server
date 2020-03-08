package com.spinpi.http.marshallers.html

import java.io.StringWriter

import com.github.mustachejava.MustacheFactory
import com.google.inject.{Inject, Singleton}

@Singleton
class MustacheService @Inject() (
    mustacheFactory: MustacheFactory
) {

  def createString(templateName: String, obj: Any): String = {
    val mustache = mustacheFactory.compile(templateName)

    val writer = new StringWriter()
    mustache.execute(writer, obj).flush()

    writer.toString
  }

}
