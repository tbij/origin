import scala.util.{Try, Success, Failure}
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import org.scalatra.{ScalatraServlet, NotFound, NoContent, BadRequest, InternalServerError}

object Routes extends ScalatraServlet {

  private def user = User.fromToken(cookies("token")).get

  private val locations = Map(
    "posts" -> Config.site.locationOfPosts,
    "pages" -> Config.site.locationOfPages,
    "data" -> Config.site.locationOfData
  )

  get("/files/*") {
    val location = locations.getOrElse(params("splat"), halt(BadRequest()))
    Site.list(location) match {
      case Success(list) => Json(list)
      case Failure(e) => NotFound("Path not found")
    }
  }

  get("/files/*/:name") {
    val location = locations.getOrElse(params("splat"), halt(BadRequest()))
    Site.read(location, params("name")) match {
      case Success(contents) => if (params("name").toLowerCase endsWith ".json") Json.parse(contents) else contents
      case Failure(e) => pass()
    }
  }

  put("/files/*/:name") {
    val location = locations.getOrElse(params("splat"), halt(BadRequest()))
    Site.write(location, params("name"), request.body) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  post("/files/*/:name") {
    val location = locations.getOrElse(params("splat"), halt(BadRequest()))
    Site.publish(location, params("name"), user) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  get("/changed/*") {
    val location = locations.getOrElse(params("splat"), halt(BadRequest()))
    Site.changed(location) match {
      case Success(list) => Json(list)
      case Failure(e) => NotFound("Path not found")
    }
  }

}
