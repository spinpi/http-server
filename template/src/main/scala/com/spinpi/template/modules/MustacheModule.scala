package com.spinpi.template.modules

import java.io.{FileNotFoundException, InputStream, InputStreamReader, Reader}

import com.github.mustachejava.{
  DefaultMustacheFactory,
  Mustache,
  MustacheFactory
}
import com.google.inject.Provides
import com.spinpi.template.mustache.{
  MustacheTemplateSettings,
  ScalaObjectHandler
}
import javax.inject.Singleton
import net.codingwell.scalaguice.ScalaModule

object MustacheModule extends ScalaModule {

  @Provides
  @Singleton
  def provideMustacheFactory(
      resolver: FileResolver,
      scalaObjectHandler: ScalaObjectHandler,
      mustacheTemplateSettings: MustacheTemplateSettings
  ): MustacheFactory = {
    import mustacheTemplateSettings._
    // templates are cached only if there is no local.doc.root
    val cacheMustacheTemplates = localDocRoot.isEmpty

    new DefaultMustacheFactory(templatesDir) {
      setObjectHandler(scalaObjectHandler)

      override def compile(name: String): Mustache = {
        if (cacheMustacheTemplates) {
          super.compile(name)
        } else {
          new LocalFilesystemDefaultMustacheFactory(
            templatesDir,
            resolver,
            scalaObjectHandler
          ).compile(name)
        }
      }
    }
  }

}

private final class LocalFilesystemDefaultMustacheFactory(
    templatesDirectory: String,
    resolver: FileResolver,
    objectHandler: ScalaObjectHandler
) extends DefaultMustacheFactory {
  setObjectHandler(objectHandler)

  override def getReader(resourceName: String): Reader = {
    // Relative paths are prefixed by the templates directory.
    val filepath = if (resourceName.startsWith("/")) {
      resourceName
    } else if (templatesDirectory.startsWith("/")) {
      s"$templatesDirectory/$resourceName"
    } else {
      s"/$templatesDirectory/$resourceName"
    }

    (resolver.getInputStream(filepath) map { inputStream: InputStream =>
      new InputStreamReader(inputStream)
    }).getOrElse(
      throw new FileNotFoundException(s"Unable to find file: $filepath")
    )
  }
}
