package com.spinpi.http.marshallers.html

import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.typesafe.config.Config
import com.spinpi.config.ConfigExtensions._

import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class MustacheTemplateSettings(
    val docRoot: String,
    val localDocRoot: String,
    val templatesDir: String,
    val futureTimeout: Duration
) {

  @Inject()
  def this(@Named("rootConfig") config: Config) = {
    this(
      config.stringOrDefault("marshaller.mustache.doc.root", ""),
      config.stringOrDefault("marshaller.mustache.local.doc.root", ""),
      config.stringOrDefault("marshaller.mustache.templates.dir", ""),
      config.durationOrDefault("marshaller.mustache.future.timeout", 3 seconds)
    )
  }
}
