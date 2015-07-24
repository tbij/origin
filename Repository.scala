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

  def publish(directory: String, file: String): Try[Unit] = {
    Try {
      val git = new Git(new FileRepository(Config.site.directory + "/.git"))
      git.add().addFilepattern(Config.site.directory + "/" + directory + "/" + file).call()
      git.commit().setMessage("Published").call()
      git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(Config.git.username, Config.git.password)).call()
    }
  }

  private def update: Unit = {
    val exists = Files.isDirectory(Paths.get(Config.site.directory))
    if (exists) new Git(new FileRepository(Config.site.directory + "/.git")).pull().call()
    else Git.cloneRepository().setDirectory(Paths.get(Config.site.directory).toFile).setURI(Config.git.repository).call().close()
  }

  private def generate: Unit = {
    Process(Config.site.build, Paths.get(Config.site.directory).toFile).!
  }

}
