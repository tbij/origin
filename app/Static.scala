import java.nio.file.{Paths, Path, Files}
import scala.io.Source
import scala.collection.JavaConversions._
import org.scalatra.{ScalatraServlet, ApiFormats, NotFound}

class Static(source: String, single: Boolean = false) extends ScalatraServlet with ApiFormats {

  addMimeMapping("image/png", "png")
  addMimeMapping("image/jpeg", "jpeg")
  addMimeMapping("image/jpeg", "jpg")

  private def list(directory: String): Iterable[Path] = {
    Files.newDirectoryStream(Paths.get(directory)) flatMap { path =>
      if (Files.isDirectory(path)) list(path.toString)
      else Seq(path)
    }
  }

  private def filename(path: String) = if (path contains '.') path.split('.').head else ""
  private def extension(path: String) = if (path contains '.') path.split('.').last else ""

  get("/*") { // note that this does not support top-level content-type negotiation (eg. image/*)
    val location = params("splat")
    val files = list(source).map(Paths.get(source).relativize).map(_.toString)
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
    val path = Paths.get(source + "/" + file)
    contentType = file.split('.').lastOption.flatMap(formats.get).getOrElse("text/plain")
    path.toFile
  }

}
