import java.util.EnumSet
import javax.servlet.DispatcherType
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, FilterHolder, DefaultServlet, ServletContextHandler}

object Start extends App {

  Site.update
  Site.generate

  val authenticator = new FilterHolder(Authenticator)

  val routes = new ServletHolder(Routes)

  val interface = new ServletHolder(new DefaultServlet())
  interface.setInitParameter("resourceBase", this.getClass.getResource("interface").toString)

  val preview = new ServletHolder(new DefaultServlet())
  preview.setInitParameter("resourceBase", Config.site.directory + "/" + Config.site.locationOfBuilt)
  preview.setInitParameter("pathInfoOnly", "true")

  val handler = new ServletContextHandler()
  handler.addFilter(authenticator, "/*", EnumSet.of(DispatcherType.REQUEST))
  handler.addServlet(routes, "/api/*")
  handler.addServlet(interface, "/*")
  handler.addServlet(preview, "/preview/*")

  val server = new Server(8000)
  server.setHandler(handler)
  server.start()

}
