import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.{ResourceHandler, ContextHandler, HandlerList}
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}

object Start extends App {

  val admin = new ResourceHandler()
  admin.setResourceBase("public")

  val preview = new ContextHandler()
  val previewResource = new ResourceHandler()
  previewResource.setResourceBase(Config.site.directory + "/" + Config.site.location)
  preview.setHandler(previewResource)
  preview.setContextPath("/preview")

  val api = new ServletContextHandler()
  api.addServlet(new ServletHolder(Dispatcher), "/*")
  api.setContextPath("/api")

  val handlers = new HandlerList()
  handlers.setHandlers(Array(admin, preview, api))

  val server = new Server(8000)
  server.setHandler(handlers)
  server.start()

}
