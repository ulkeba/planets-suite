/**
 * 
 */
package eu.planets_project.ifr.core.storage.impl.bl.dls.uitls;

import java.io.IOException;
import java.net.*;

import org.apache.soap.*;
import org.apache.soap.messaging.*;
import javax.activation.*;
import javax.mail.MessagingException;

/**
 * @author carl
 *
 */
public class GetServiceProxy {
	private static final String SERVICE_LOC = "http://localhost:1824/Access.asmx?wsdl";
	private static final String SERVICE_URI = "http://bl.uk/Get";
	private URL serviceLocation = null;
	private String soapUri = "";
	private Message message = new Message();
	private Envelope envelope = new Envelope();
	private DataHandler returnHandler = null;
	
	/**
	 * @throws MalformedURLException
	 */
	public GetServiceProxy() throws MalformedURLException {
		this.serviceLocation = new URL(SERVICE_LOC);
	}
	
	/**
	 * @return
	 */
	public synchronized URL getServiceLocation() {
		return this.serviceLocation;
	}
	
	/**
	 * @param domId
	 * @param retFileName
	 * @return
	 * @throws SOAPException
	 * @throws MessagingException 
	 * @throws IOException 
	 */
	public synchronized boolean submitGetRequest(String sequenceId, String domId, String retFileName) throws SOAPException, MessagingException, IOException {
	    if (this.serviceLocation == null) 
	    {
	      throw new SOAPException (Constants.FAULT_CODE_CLIENT, "A URL not specified");
	    }
	    this.soapUri = SERVICE_URI;
	    GetMessageBody ourBody = new GetMessageBody(sequenceId, domId, retFileName);
	    this.envelope.setBody(ourBody);
	    this.message.send (this.getServiceLocation(), this.soapUri, this.envelope);
        this.returnHandler = this.message.receive();
	    String serviceResult = this.returnHandler.getContent().toString();
	    return serviceResult.equals("true");
	}
}
