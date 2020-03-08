package com.spinpi.config

import com.typesafe.config.{Config, ConfigException}

import scala.concurrent.duration._
import scala.util.Try

object ConfigExtensions {

  implicit class RichConfig(config: Config) {
    def stringOrDefault(key: String, default: String): String = {
      valueOrDefault(config.getString(key), default)
    }

    def durationOrDefault(key: String, default: Duration): Duration = {
      valueOrDefault({
        val millis = config.getDuration(key, MILLISECONDS)
        Duration(millis, MILLISECONDS)
      }, default)
    }

  }

  def valueOrDefault[T](resolver: => T, default: T): T = {
    Try(resolver).recover {
      case _: ConfigException.Missing => default
    }.get
  }
}
