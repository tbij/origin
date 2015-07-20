import scala.io.Source
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._

object Config {

  private val config = Json.parse(Source.fromFile("config.json").mkString)

  val repository = config.repository.as[String]
  val username = config.username.as[String]
  val password = config.password.as[String]

}
