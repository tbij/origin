import java.net.URI
import java.nio.file.{FileSystems, FileSystem, Path, Files}
import scala.util.Try
import scala.io.Source
import scala.collection.JavaConversions._
import org.scalatra.{ScalatraServlet, ApiFormats, NotFound}

class Static(source: String, single: Boolean = false) extends ScalatraServlet with ApiFormats {

  addMimeMapping("image/png", "png")
  addMimeMapping("image/jpeg", "jpeg")
  addMimeMapping("image/jpeg", "jpg")

  private def list(fileSystem: FileSystem, directory: String): Iterable[Path] = {
    Files.newDirectoryStream(fileSystem.getPath(directory)) flatMap { path =>
      if (Files.isDirectory(path)) list(fileSystem, path.toString)
      else Seq(path)
    }
  }

  private def filename(path: String) = if (path contains '.') path.split('.').head else ""
  private def extension(path: String) = if (path contains '.') path.split('.').last else ""

  get("/*") { // note that this does not support top-level content-type negotiation (eg. image/*)
    val location = params("splat")
    val (filePath, fileSystem) = {
      if (source startsWith "jar:file") {
        val sourcePath = source.split(".jar!")
        (sourcePath.last, FileSystems.newFileSystem(new URI(sourcePath.head + ".jar"), Map[String, String]()))
      }
      else (source, FileSystems.getDefault)
    }
    val files = list(fileSystem, filePath).map(fileSystem.getPath(filePath).relativize).map(_.toString)
    val filesTyped = files.groupBy(filename).mapValues(_ map extension)
    val file = location match {
      case "" => "index.html"
      case f if files contains f => f
      case f if acceptHeader.find(filesTyped.get(f).map(formats get _).contains).isDefined => {
        f + "." + acceptHeader.flatMap(filesTyped(f).groupBy(formats).mapValues(_.head).get).head
      }
      case f if filesTyped.contains(f) && acceptHeader.contains("*/*") => f + "." + filesTyped(f).head
      case _ if single => "index.html" // for single-page applications
      case _ => halt(NotFound())
    }
    val path = fileSystem.getPath(filePath + "/" + file)
    Try(fileSystem.close()) // will fail for default filesystem
    contentType = file.split('.').lastOption.flatMap(formats.get).getOrElse("text/plain")
    path.toUri.toURL.openStream
  }

}
