import scala.util.{Try, Success, Failure}
import rapture.json.Json
import rapture.json.jsonBackends.argonaut._
import org.scalatra.ScalatraServlet
import org.scalatra.{NotFound, NoContent, InternalServerError}

object Routes extends ScalatraServlet {

  get("/*") {
    Site.list(params("splat")) match {
      case Success(list) => Json(list)
      case Failure(e) => NotFound("Path not found")
    }
  }

  get("/*/:file") {
    Site.read(params("splat"), params("file")) match {
      case Success(contents) => if (params("file").toLowerCase endsWith ".json") Json.parse(contents) else contents
      case Failure(e) => pass()
    }
  }

  put("/*/:file") {
    Site.write(params("splat"), params("file"), request.body) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

  post("/*/:file") {
    Site.publish(params("splat"), params("file")) match {
      case Success(_) => NoContent()
      case Failure(e) => InternalServerError(e)
    }
  }

}
