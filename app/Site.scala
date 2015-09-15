import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.io.Source
import scala.collection.JavaConversions._
import scala.sys.process.Process
import scala.util.Try
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.internal.storage.file.FileRepository

object Site {

  case class State(hasChanged: Boolean)

  def list(directory: String): Try[Seq[String]] = {
    Try {
      update
      val path = Paths.get(Config.site.directory + "/" + directory)
      Files.newDirectoryStream(path).toSeq.map(_.toString.split("/").last)
    }
  }

  def read(directory: String, file: String): Try[String] = {
    Try {
      update
      Source.fromFile(Config.site.directory + "/" + directory + "/" + file).mkString
    }
  }

  def write(directory: String, file: String, contents: String): Try[Unit] = {
    Try {
      update
      val path = Paths.get(Config.site.directory + "/" + directory + "/" + file)
      Files.write(path, contents.getBytes(StandardCharsets.UTF_8))
      generate
    }
  }

  def publish(directory: String, file: String, user: User): Try[Unit] = {
    Try {
      update
      val git = new Git(new FileRepository(Config.site.directory + "/.git"))
      git.add.addFilepattern(directory + "/" + file).call()
      git.commit.setAuthor(user.name, user.email).setMessage("Published").call()
      git.push.setCredentialsProvider(new UsernamePasswordCredentialsProvider(Config.git.username, Config.git.password)).call()
    }
  }

  def state(directory: String, file: String): Try[State] = {
    Try {
      val git = new Git(new FileRepository(Config.site.directory + "/.git"))
      val hasChanged = !git.status.addPath(directory + "/" + file).call().getChanged().isEmpty
      State(hasChanged)
    }
  }

  def update: Unit = {
    val exists = Files.isDirectory(Paths.get(Config.site.directory))
    if (exists) new Git(new FileRepository(Config.site.directory + "/.git")).pull.call()
    else Git.cloneRepository.setDirectory(Paths.get(Config.site.directory).toFile).setURI(Config.git.repository).call().close()
  }

  def generate: Unit = {
    Process(Config.site.builder, Paths.get(Config.site.directory).toFile).!
  }

}
