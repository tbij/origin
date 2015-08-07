import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext
import dispatch.{Http, url => Url, as, enrichFuture}
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import org.scalatra.{ScalatraFilter, CookieOptions, Ok, Found, BadRequest, InternalServerError}

object Authenticator extends ScalatraFilter {

  implicit val context = ExecutionContext.global
  override implicit val cookieOptions = CookieOptions(path = "/", maxAge = 60 * 60 * 24 * 7) // expires in a week

  before() {
    response.setHeader("Cache-Control", "no-store")
    val isProtected = request.getRequestURI != "/sign-in" && request.getRequestURI != "/sign-in/authenticate"
    val user = cookies.get("token").flatMap(User.fromToken)
    if (isProtected && user.isEmpty) redirect(url("/sign-in", Map("next" -> request.getRequestURI)))
    else if (request.getMethod != "GET") { // require Auth header for non-GET requests (because of CSRF)
      val authorisation = request.headers.getOrElse("Authorization", halt(BadRequest()))
      if (authorisation.stripPrefix("Bearer ") != cookies("token")) halt(BadRequest())
    }
    if (user.isDefined) cookies.set("token", user.get.token)
  }

  get("/sign-in") {
    val next = params.get("next").filterNot(_.isEmpty).getOrElse("/")
    val authParams = Map(
      "scope" -> "openid profile email",
      "response_type" -> "code",
      "client_id" -> Config.auth.clientId,
      "hd" -> Config.auth.domain,
      "state" -> next,
      "redirect_uri" -> fullUrl("/sign-in/authenticate")
    )
    Found(relativeUrl("https://accounts.google.com/o/oauth2/v2/auth", authParams))
  }

  get("/sign-in/authenticate") {
    val code = params.getOrElse("code", halt(BadRequest()))
    val next = params.getOrElse("state", halt(BadRequest()))
    Try {
      val tokensParams = Map(
        "code" -> code,
        "client_id" -> Config.auth.clientId,
        "client_secret" -> Config.auth.clientSecret,
        "redirect_uri" -> fullUrl("/sign-in/authenticate"),
        "state" -> next,
        "grant_type" -> "authorization_code"
      )
      val tokens = Json.parse(Http(Url("https://www.googleapis.com/oauth2/v4/token") << tokensParams OK as.String).apply())
      val lookupParams = Map("access_token" -> tokens.access_token.as[String])
      val lookup = Json.parse(Http(Url("https://www.googleapis.com/oauth2/v3/userinfo") <<? lookupParams OK as.String).apply())
      assert(lookup.hd.as[Option[String]] == Some(Config.auth.domain))
      val user = User(lookup.name.as[String], lookup.email.as[String])
      cookies.set("token", user.token)
    }
    match {
      case Success(_) => Ok(headers = Map("Refresh" -> s"0; url=$next"))
      case Failure(e: AssertionError) => BadRequest()
      case Failure(e: rapture.data.MissingValueException) => BadRequest() // due to bug: https://github.com/propensive/rapture-json/issues/23
      case Failure(e) => InternalServerError(e, Map("Refresh" -> s"0; url=$next"))
    }
  }

}
