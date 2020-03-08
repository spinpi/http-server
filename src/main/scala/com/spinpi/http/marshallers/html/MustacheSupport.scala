package com.spinpi.http.marshallers.html

import akka.http.scaladsl.marshalling.{
  Marshaller,
  Marshalling,
  ToEntityMarshaller
}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MessageEntity}
import com.spinpi.http.response.MustacheResponse

import scala.concurrent.Future

trait MustacheSupport {
  val mustacheService: MustacheService

  implicit def defaultMustacheMarshaller
      : ToEntityMarshaller[MustacheResponse] = {
    Marshaller
      .apply[MustacheResponse, MessageEntity] { implicit ec => resp =>
        Future {
          List(
            Marshalling.WithFixedContentType(
              ContentTypes.`text/html(UTF-8)`,
              () =>
                HttpEntity.apply(
                  ContentTypes.`text/html(UTF-8)`,
                  mustacheService.createString(resp.template, resp.data)
                )
            )
          )
        }
      }
  }
}
