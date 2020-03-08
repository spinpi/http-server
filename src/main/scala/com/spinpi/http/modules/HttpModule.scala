package com.spinpi.http.modules

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.google.inject.{Provides, Singleton}
import com.google.inject.name.Named
import com.typesafe.config.{Config, ConfigFactory}
import net.codingwell.scalaguice.ScalaModule
import com.spinpi.config.ConfigExtensions._
import com.spinpi.http.marshallers.html.MustacheTemplateSettings

object HttpModule extends ScalaModule {

  override def configure(): Unit = {
    super.configure()

    val rootConfig = ConfigFactory.load()

    bind[Config]
      .annotatedWithName("rootConfig")
      .toInstance(rootConfig)

  }

  @Provides
  @Singleton
  def providesActorSystem(
      @Named("ApplicationName") appName: String
  ): ActorSystem = {
    ActorSystem(appName)
  }

  @Provides
  @Singleton
  def providesActorMaterializer(actorSystem: ActorSystem): Materializer = {
    Materializer(actorSystem)
  }

  @Provides
  @Named("ApplicationName")
  def providesApplicationName(@Named("rootConfig") config: Config): String = {
    config.stringOrDefault("application.name", "Application")
  }

}
