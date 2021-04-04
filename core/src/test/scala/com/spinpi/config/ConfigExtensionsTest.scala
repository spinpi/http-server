package com.spinpi.config

import com.typesafe.config.{ConfigException, ConfigFactory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks

class ConfigExtensionsTest
    extends AnyFlatSpec
    with TableDrivenPropertyChecks
    with should.Matchers {

  "valueOrDefault" should "resolve value correctly" in {
    ConfigExtensions.valueOrDefault("hello", "default") shouldEqual "hello"

    ConfigExtensions.valueOrDefault(
      throw new ConfigException.Missing("hello"),
      "default"
    ) shouldEqual "default"

    intercept[ConfigException.BadValue] {
      ConfigExtensions.valueOrDefault(
        throw new ConfigException.BadValue("key", "bad value"),
        "default"
      )
    }
  }

  "RichConfig.stringOrDefault" should "get value correctly" in {
    import ConfigExtensions._
    val config = ConfigFactory.parseString("""{
        |  "string": "aa",
        |  "number": 100
        |}""".stripMargin)

    config.stringOrDefault("string", "default") shouldEqual "aa"
    config.stringOrDefault("number", "default") shouldEqual "100"
    config.stringOrDefault("not_found", "default") shouldEqual "default"
  }

  "RichConfig.durationOrDefault" should "get value correctly" in {
    import ConfigExtensions._

    import scala.concurrent.duration._
    val config = ConfigFactory.parseString("""{
                                             |  "string": "aa",
                                             |  "duration": 2 seconds
                                             |}""".stripMargin)

    config.durationOrDefault("duration", 1.second) shouldEqual 2.seconds
    config.durationOrDefault("not_found", 1.second) shouldEqual 1.second
    intercept[ConfigException.BadValue] {
      config.durationOrDefault("string", 1.second)
    }
  }

}
