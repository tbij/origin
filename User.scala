import scala.util.Try
import scala.collection.JavaConversions._
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import com.auth0.jwt.{JWTSigner, Algorithm, JWTVerifier}

case class User(name: String, email: String) {

  def token: String = {
    val claims = Map("content" -> Json(this).toString)
    val options = new JWTSigner.Options().setAlgorithm(Algorithm.HS256).setIssuedAt(true).setExpirySeconds(60 * 60 * 24 * 7) // expires in a week
    new JWTSigner(Config.auth.key).sign(claims, options)
  }

}

object User {

  def fromToken(token: String): Option[User] = {
    val attempt = Try {
      val decoded = new JWTVerifier(Config.auth.key).verify(token)
      Json.parse(decoded("content").toString).as[User]
    }
    attempt.toOption
  }

}
