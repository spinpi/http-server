package com.spinpi.conversions

object string {
  implicit class RichString(val self: String) extends AnyVal {
    def toOption: Option[String] = {
      if (self == null || self.isEmpty)
        None
      else
        Some(self)
    }
  }
}
