import scala.util.{Try, Success, Failure}
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import org.scalatra.{ScalatraServlet, NotFound, NoContent, InternalServerError}

object Routes extends ScalatraServlet {

  private def user = User.fromToken(cookies("token")).get

  private val locations = Map(
    "posts" -> Config.site.locationOfPosts,
    "pages" -> Config.site.locationOfPages,
    "data" -> Config.site.locationOfData
  )

  get("/*") {
    val location = locations(params("splat"))
    Site.list(location) match {
      case Success(list) => Json(list)
      case Failure(e) => NotFound("Path not found")
    }
  }

  get("/*/:file") {
    val location = locations(params("splat"))
    Site.read(location, params("file")) match {
      case Success(contents) => if (params("file").toLowerCase endsWith ".json") Json.parse(contents) else contents
      case Failure(e) => pass()
    }
  }

  put("/*/:file") {
    val location = locations(params("splat"))
    Site.write(location, params("file"), request.body) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  post("/*/:file") {
    val location = locations(params("splat"))
    Site.publish(location, params("file"), user) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

}
