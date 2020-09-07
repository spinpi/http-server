package com.spinpi.graphql

import java.nio.charset.Charset

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import sangria.ast.Document
import sangria.parser.QueryParser
import sangria.renderer.{QueryRenderer, QueryRendererConfig}

import scala.collection.immutable.Seq

object RequestUnmarshaller {

  val `application/graphql`: MediaType.WithFixedCharset = MediaType
    .applicationWithFixedCharset("graphql", HttpCharsets.`UTF-8`, "graphql")

  def mediaTypes: Seq[MediaType.WithFixedCharset] =
    List(`application/graphql`)

  def unmarshallerContentTypes: Seq[ContentTypeRange] =
    mediaTypes.map(ContentTypeRange.apply)

  implicit final def documentMarshaller(
      implicit config: QueryRendererConfig = QueryRenderer.Compact
  ): ToEntityMarshaller[Document] =
    Marshaller.oneOf(mediaTypes: _*) { mediaType ⇒
      Marshaller.withFixedContentType(ContentType(mediaType)) { json ⇒
        HttpEntity(mediaType, QueryRenderer.render(json, config))
      }
    }

  implicit final val documentUnmarshaller: FromEntityUnmarshaller[Document] =
    Unmarshaller.byteStringUnmarshaller
      .forContentTypes(unmarshallerContentTypes: _*)
      .map {
        case ByteString.empty ⇒ throw Unmarshaller.NoContentException
        case data ⇒
          QueryParser.parse(data.decodeString(Charset.forName("UTF-8"))).get
      }

}
