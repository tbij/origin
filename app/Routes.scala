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

  get("/files/*") {
    val location = locations(params("splat"))
    Site.list(location) match {
      case Success(list) => Json(list)
      case Failure(e) => NotFound("Path not found")
    }
  }

  get("/files/*/:name") {
    val location = locations(params("splat"))
    Site.read(location, params("name")) match {
      case Success(contents) => if (params("name").toLowerCase endsWith ".json") Json.parse(contents) else contents
      case Failure(e) => pass()
    }
  }

  put("/files/*/:name") {
    val location = locations(params("splat"))
    Site.write(location, params("name"), request.body) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  post("/files/*/:name") {
    val location = locations(params("splat"))
    Site.publish(location, params("name"), user) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  get("/state/*/:name") {
    val location = locations(params("splat"))
    Site.state(location, params("name")) match {
      case Success(state) => Json(state)
      case Failure(e) => NotFound("Path not found")
    }
  }

}
