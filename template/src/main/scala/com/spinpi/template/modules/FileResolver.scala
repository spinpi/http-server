package com.spinpi.template.modules

import com.google.inject.{Inject, Singleton}
import com.spinpi.conversions.boolean.RichBoolean
import com.spinpi.template.mustache.MustacheTemplateSettings
import com.typesafe.scalalogging.LazyLogging
import jakarta.activation.MimetypesFileTypeMap
import org.apache.commons.io.FilenameUtils

import java.io.{BufferedInputStream, File, FileInputStream, InputStream}

@Singleton
class FileResolver @Inject() (
    mustacheTemplateSettings: MustacheTemplateSettings
) extends LazyLogging {
  import mustacheTemplateSettings._
  // assertions -- cannot have both doc roots set
  if (localDocRoot.nonEmpty && docRoot.nonEmpty) {
    throw new java.lang.AssertionError(
      "assertion failed: Cannot set both marshaller.mustache.doc.root and marshaller.mustache.local.doc.root config."
    )
  }

  private val extMap        = new MimetypesFileTypeMap()
  private val localFileMode = localDocRoot.nonEmpty.onTrue {
    logger.info("Local file mode enabled")
  }
  private val actualDocRoot =
    if (docRoot.startsWith("/")) docRoot else "/" + docRoot

  /* Public */

  def getInputStream(path: String): Option[InputStream] = {
    assert(path.startsWith("/"))
    if (isDirectory(path))
      None
    else if (localFileMode)
      getLocalFileInputStream(path)
    else
      getClasspathInputStream(path)
  }

  def getContentType(file: String): String = {
    extMap.getContentType(dottedFileExtension(file))
  }

  /* Private */

  private def isDirectory(path: String): Boolean = {
    path.endsWith("/")
  }

  private def getClasspathInputStream(path: String): Option[InputStream] = {
    val actualPath = if (docRoot.nonEmpty) s"$actualDocRoot$path" else path
    for {
      is <- Option(getClass.getResourceAsStream(actualPath))
      bis = new BufferedInputStream(is)
      if bis.available > 0
    } yield bis
  }

  private def getLocalFileInputStream(path: String): Option[InputStream] = {
    // try absolute path first, then under local.doc.root
    val file = new File(path) match {
      case f if f.exists() => f
      case _               => new File(localDocRoot, path)
    }

    if (file.exists)
      Option(new BufferedInputStream(new FileInputStream(file)))
    else
      None
  }

  private def dottedFileExtension(uri: String): String = {
    '.' + FilenameUtils.getExtension(uri)
  }
}
