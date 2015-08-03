import scala.util.Try
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

case class User(name: String, email: String) {

  def token: String = {
    val content = Json(this).toString
    val claims = JwtClaim(content = content).issuedNow.expiresIn(60 * 60 * 24 * 7) // expires in a week
    Jwt.encode(Json(claims).toString, Config.auth.key, JwtAlgorithm.HS256)
  }

}

object User {

  def fromToken(token: String): Option[User] = {
    val attempt = Try {
      val decoded = Jwt.decode(token, Config.auth.key, Seq(JwtAlgorithm.HS256)).get
      val claims = Json.parse(decoded).as[JwtClaim]
      Json.parse(claims.content).as[User]
    }
    attempt.toOption
  }

}
