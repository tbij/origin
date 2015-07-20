import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.server.handler.{ResourceHandler, HandlerList}

object Start extends App {

  val resourceHandler = new ResourceHandler()
  resourceHandler.setResourceBase("public")

  val servletHandler = new ServletContextHandler()
  servletHandler.addServlet(new ServletHolder(Dispatcher), "/*")
  servletHandler.setContextPath("/api/")

  val handlers = new HandlerList()
  handlers.setHandlers(Array(servletHandler, resourceHandler))

  val server = new Server(8000)
  server.setHandler(handlers)
  server.start()

}
