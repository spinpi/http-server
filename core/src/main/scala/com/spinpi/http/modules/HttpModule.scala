package com.spinpi.http.modules

import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import com.google.inject.Provides
import com.google.inject.name.Named
import com.google.inject.Singleton
import com.spinpi.config.ConfigExtensions.RichConfig
import com.typesafe.config.{Config, ConfigFactory}
import net.codingwell.scalaguice.ScalaModule

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
    SystemMaterializer(actorSystem).materializer
  }

  @Provides
  @Named("ApplicationName")
  def providesApplicationName(@Named("rootConfig") config: Config): String = {
    config.stringOrDefault("application.name", "Application")
  }

}
