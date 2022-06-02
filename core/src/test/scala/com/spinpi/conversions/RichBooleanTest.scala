package com.spinpi.conversions

import com.spinpi.conversions.boolean.RichBoolean
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks

class RichBooleanTest
    extends AnyFlatSpec
    with should.Matchers
    with TableDrivenPropertyChecks {
  "option" should "correct" in {

    forAll(
      Table(
        ("input", "fn", "expected", "invoked"),
        (true, () => "value", Some("value"), true),
        (false, () => "value", None, false)
      )
    ) { (input, fn, expected, invoked) =>
      var invokeFlag = false
      input.option {
        invokeFlag = true
        fn()
      } shouldBe expected

      invokeFlag shouldBe invoked
    }
  }

  "onTrue" should "correct" in {
    forAll(
      Table(
        ("input", "invoked"),
        (true, true),
        (false, false)
      )
    ) { (input, invoked) =>
      var invokeFlag = false
      input.onTrue {
        invokeFlag = true
      } shouldBe input

      invokeFlag shouldBe invoked
    }
  }

  "onFalse" should "correct" in {
    forAll(
      Table(
        ("input", "invoked"),
        (true, false),
        (false, true)
      )
    ) { (input, invoked) =>
      var invokeFlag = false
      input.onFalse {
        invokeFlag = true
      } shouldBe input

      invokeFlag shouldBe invoked
    }
  }
}
