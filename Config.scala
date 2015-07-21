import scala.io.Source
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._

object Config {

  private val config = Json.parse(Source.fromFile("config.json").mkString)

  object git {
    val repository = config.git.repository.as[String]
    val username = config.git.username.as[String]
    val password = config.git.password.as[String]
  }

  object site {
    val build = config.site.build.as[String]
    val directory = config.site.directory.as[String]
    val location = config.site.location.as[String]
  }

}
