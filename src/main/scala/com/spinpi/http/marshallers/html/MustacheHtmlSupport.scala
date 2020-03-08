package com.spinpi.http.marshallers.html

import akka.http.scaladsl.marshalling.{
  Marshaller,
  Marshalling,
  ToEntityMarshaller
}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MessageEntity}
import com.spinpi.http.response.MustacheResponse

import scala.concurrent.Future

trait MustacheHtmlSupport {
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

//object MustacheHtmlSupport {
//  val templatesDirectory     = ""
//  val cacheMustacheTemplates = false
//  val mustacheFactory: MustacheFactory = new DefaultMustacheFactory(
//    templatesDirectory
//  ) {
//    setObjectHandler(new ScalaObjectHandler)
//
//    override def compile(name: String): Mustache = {
//      if (cacheMustacheTemplates) {
//        super.compile(name)
//      } else {
//        new LocalFilesystemDefaultMustacheFactory(templatesDirectory, resolver)
//          .compile(name)
//      }
//    }
//  }
//}
//
///**
//  * A local filesystem-only MustacheFactory. Uses the FileResolver for resolution and
//  * does not internally cache templates.
//  */
//private final class LocalFilesystemDefaultMustacheFactory(
//    templatesDirectory: String,
//    resolver: FileResolver
//) extends DefaultMustacheFactory {
//  setObjectHandler(new ScalaObjectHandler)
//
//  override def getReader(resourceName: String): Reader = {
//    // Relative paths are prefixed by the templates directory.
//    val filepath = if (resourceName.startsWith("/")) {
//      resourceName
//    } else if (templatesDirectory.startsWith("/")) {
//      s"$templatesDirectory/$resourceName"
//    } else {
//      s"/$templatesDirectory/$resourceName"
//    }
//
//    (resolver.getInputStream(filepath) map { inputStream: InputStream =>
//      new InputStreamReader(inputStream)
//    }).getOrElse(
//      throw new FileNotFoundException(s"Unable to find file: $filepath")
//    )
//  }
//}
