import java.util.EnumSet
import javax.servlet.DispatcherType
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, FilterHolder, ServletContextHandler}

object Start extends App {

  Site.update
  Site.generate

  private val locationOfPreview = Config.site.directory + "/" + Config.site.locationOfBuilt
  private val locationOfInterface = this.getClass.getResource("interface").toExternalForm

  private val handler = new ServletContextHandler()
  handler.addFilter(new FilterHolder(Authenticator), "/*", EnumSet.of(DispatcherType.REQUEST))
  handler.addServlet(new ServletHolder(Routes), "/api/*")
  handler.addServlet(new ServletHolder(new Static(locationOfPreview)), "/preview/*") // todo will break without trailing slash -- fix with <base href>?
  handler.addServlet(new ServletHolder(new Static(locationOfInterface, single = true)), "/*")

  private val server = new Server(8000)
  server.setHandler(handler)
  server.start()
  server.join()

}
