import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.io.Source
import scala.collection.JavaConversions._
import scala.sys.process.Process
import scala.util.Try
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.internal.storage.file.FileRepository

object Repository {

  def list(directory: String): Try[Seq[String]] = {
    update
    Try {
      val path = Paths.get(Config.site.directory + "/" + directory)
      Files.newDirectoryStream(path).toSeq.map(_.toString.split("/").last)
    }
  }

  def read(directory: String, file: String): Try[String] = {
    update
    Try {
      Source.fromFile(Config.site.directory + "/" + directory + "/" + file).mkString
    }
  }

  def write(directory: String, file: String, contents: String): Try[Unit] = {
    update
    Try {
      val path = Paths.get(Config.site.directory + "/" + directory + "/" + file)
      Files.write(path, contents.getBytes(StandardCharsets.UTF_8))
      generate
    }
  }

  private def update: Unit = {
    val credentials = new UsernamePasswordCredentialsProvider(Config.git.username, Config.git.password)
    if (Files.isDirectory(Paths.get(Config.site.directory))) new Git(new FileRepository(Config.site.directory + "/.git"))
      .pull()
      .setCredentialsProvider(credentials)
      .call()
    else Git.cloneRepository()
      .setDirectory(Paths.get(Config.site.directory).toFile)
      .setURI(Config.git.repository)
      .setCredentialsProvider(credentials)
      .call()
      .close()
  }

  private def generate: Unit = {
    Process(Config.site.build, Paths.get(Config.site.directory).toFile).!
  }

}
