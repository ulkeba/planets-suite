
package eu.planets_project.ifr.core.wee.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.spi.management.EndpointRegistry;

/**
 * The servlet that is associated with context /wee-monitor
 * 
 * This code no longer supported by JBoss.  Use JMX instead. 
 * 
 * See http://jbws.dyndns.org/mediawiki/index.php?title=Endpoint_management
 *
 * @author rainer.schuster@researchstudio.at
 * @since 11-Jun-2007
 */
public abstract class MonitoringContextServlet extends HttpServlet
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 6123588268584270087L;

// provide logging
   protected final Logger log = Logger.getLogger(MonitoringContextServlet.class);

//   protected ServiceEndpointManager epManager;
   protected EndpointRegistry epManager;


   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      initServiceEndpointManager();
   }

   protected abstract void initServiceEndpointManager();

   /** Process GET requests.
    */
   public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      PrintWriter writer = res.getWriter();
      res.setContentType("text/html");

      writer.print("<html>");
      setupHTMLResponseHeader(writer, req);

      writer.print("<body style='font-size:1.0em;'>");
      writer.print("<div id='wrapper'><a name='top'></a> <span class='hide'><a href='#content'>Skip Navigation / Jump to Content</a></span>");
	  writer.print("<div id='header'>");
	  writer.print("<div class='logo'><a href='/'>");
	  writer.print("<img style='border: 0px solid ; ' src='images/Planets_Logo.png' alt='Planets: Home' title='Planets: Home' /></a>");
	  writer.print("</div><span class='sitetitle'>PLANETS Workflow Monitor</span> </div>");


      writer.print("<div id='content'><a name='content'></a>");
      writer.print("<fieldset>");
      writer.print("<legend><b>Deployed PLANETS Workflow Endpoints</b></legend>");
      writer.print("<table>");

      // begin iteration
      
      Set<ObjectName> endpoints = epManager.getEndpoints(); //getRegisteredEndpoints(requestURL);

      if(endpoints.isEmpty())
      {
         writer.print("<tr>");
         writer.print("	<td><h3>There are currently no PLANETS workflows deployed</h3></td>");
         writer.print("</tr>");
      }

      for(ObjectName on : endpoints)
      {
    	 Endpoint ep = epManager.getEndpoint(on);
    	 if(ep.getAddress().endsWith("Workflow")){
	         writer.print("<tr>");
	         writer.print("	<td>ServiceEndpointID</td>");
	         writer.print("	<td>"+ep.getProperty(Endpoint.SEPID_DOMAIN_ENDPOINT)+"</td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td>ServiceEndpointAddress</td>");
	         writer.print("	<td><a href='"+ep.getAddress()+"?wsdl'>"+ep.getAddress()+"?wsdl</a></td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td colspan=2>");
	         writer.print("	");
	         writer.print("<fieldset><legend>PLANETS Workflow Data</legend>");
	         writer.print("<table width='650' class='item-date'>");
	         writer.print("<tr>");
	         writer.print("	<td><b>StartTime</b></td>");
	         writer.print("	<td><b>StopTime</b></td>");
	         writer.print("	<td></td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getStartTime()+"</td>");
	
	         String stopTime = ep.getEndpointMetrics().getStopTime() != null ? ep.getEndpointMetrics().getStopTime().toString() : "";
	         writer.print("	<td>"+stopTime+"</td>");
	         writer.print("	<td></td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	
	         writer.print("	<td><b>RequestCount</b></td>");
	         writer.print("	<td><b>ResponseCount</b></td>");
	         writer.print("	<td><b>FaultCount</b></td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getRequestCount()+"</td>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getResponseCount()+"</td>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getFaultCount()+"</td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td><b>MinProcessingTime</b></td>");
	         writer.print("	<td><b>MaxProcessingTime</b></td>");
	         writer.print("	<td><b>AvgProcessingTime</b></td>");
	         writer.print("</tr>");
	         writer.print("<tr>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getMinProcessingTime()+"</td>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getMaxProcessingTime()+"</td>");
	         writer.print("	<td>"+ep.getEndpointMetrics().getAverageProcessingTime()+"</td>");
	         writer.print("</tr>");
	         writer.print("");
	         writer.print("");
	         writer.print("</table></fieldset>");
	         writer.print("");
	         writer.print("	</td>");
	         writer.print("</tr>");
	
	         writer.print("<tr><td colspan='3'>&nbsp;</td></tr>");
    	 }
      }
      
      // end iteration
      writer.print("</table>");
      writer.print("");
      writer.print("</fieldset>");
      writer.print("</div></div>");


      writer.print("</body>");
      writer.print("</html>");
      writer.close();
   }

   private void setupHTMLResponseHeader(PrintWriter writer, HttpServletRequest req)
   {
      writer.println("<head>");
      writer.println("<meta http-equiv='Content-Type content='text/html; charset=iso-8859-1'>");
      writer.println("<title>PLANETS Workflows</title>");
      writer.println("<link rel='stylesheet' href='css/screen_planets.css' media='screen'>");
      writer.println("<link rel='stylesheet' href='css/print_planets.css' media='print'>");
      writer.println("<link rel='shortcut icon' href='images/planets.ico' />");
      writer.println("</head>");
   }
}
