package com.spinpi.conversions

import com.spinpi.conversions.string.RichString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks

class RichStringTest
    extends AnyFlatSpec
    with should.Matchers
    with TableDrivenPropertyChecks {
  "toOption" should "correct" in {

    forAll(
      Table(
        ("input", "expected"),
        ("", None),
        ("Hello", Some("Hello")),
        (null: String, None)
      )
    ) { (input, expected) =>
      input.toOption shouldBe expected
    }
  }

}
