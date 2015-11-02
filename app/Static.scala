import java.net.URI
import java.nio.file.{FileSystems, FileSystem, Path, Files}
import scala.io.Source
import scala.collection.JavaConversions._
import org.scalatra.{ScalatraServlet, ApiFormats, NotFound}

class Static(source: String, single: Boolean = false) extends ScalatraServlet with ApiFormats {

  addMimeMapping("image/png", "png")
  addMimeMapping("image/jpeg", "jpeg")
  addMimeMapping("image/jpeg", "jpg")
  addMimeMapping("image/svg+xml", "svg")

  private val (fileSystem, base) = {
    if (source startsWith "jar:file") {
      val sourcePath = source.split(".jar!")
      (FileSystems.newFileSystem(new URI(sourcePath.head + ".jar"), Map.empty[String, String]), sourcePath.last)
    }
    else (FileSystems.getDefault, source.replace("file:", ""))
  }

  private def list(fileSystem: FileSystem, directory: String): Iterable[Path] = {
    Files.newDirectoryStream(fileSystem.getPath(directory)) flatMap { path =>
      if (Files.isDirectory(path)) list(fileSystem, path.toString)
      else Seq(path)
    }
  }

  get("/*") {
    val location = params("splat")
    val files = list(fileSystem, base).map(fileSystem.getPath(base).relativize).map(_.toString)
    val file = location match {
      case "" => "index.html"
      case f if files.contains(f) => f
      case f if files.contains(f + ".html") => f + ".html"
      case _ if single => "index.html" // for single-page applications
      case _ => halt(NotFound())
    }
    contentType = formats.get(file.split('.').last).getOrElse("text/plain")
    val filePath = fileSystem.getPath(base + "/" + file)
    Files.newInputStream(filePath)
  }

}
