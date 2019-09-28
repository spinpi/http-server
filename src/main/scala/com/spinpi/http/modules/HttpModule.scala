package com.spinpi.http.modules

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.{Provides, Singleton}
import com.google.inject.name.Named
import com.typesafe.config.{Config, ConfigFactory}
import net.codingwell.scalaguice.ScalaModule
import com.spinpi.config.ConfigExtensions._

object HttpModule extends ScalaModule {

  @Provides
  @Singleton
  def providesActorSystem(
      @Named("ApplicationName") appName: String
  ): ActorSystem = {
    ActorSystem(appName)
  }

  @Provides
  @Singleton
  def providesActorMaterializer(
      implicit actorSystem: ActorSystem
  ): ActorMaterializer = {
    ActorMaterializer()
  }

  @Provides
  @Named("ApplicationName")
  def providesApplicationName(@Named("rootConfig") config: Config): String = {
    config.stringOrDefault("application.name", "Application")
  }

  @Provides
  @Named("rootConfig")
  @Singleton
  def providesRootConfig(): Config = {
    ConfigFactory.load()
  }

}
