/**
 * 
 */
package eu.planets_project.services.datatypes;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A entity to hold metadata about services.  The content of this object was first 
 * defined at the IF meeting in September 2008.
 *  
 * This is intended to be used primarily as an XML schema, 
 * but is defined in Java to make reading/writing easier.
 *  
 * See also, DOAP: http://trac.usefulinc.com/doap
 * 
 * ******************* NOT IN SERVICE AT PRESENT **************************
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement(name="ServiceDescription", namespace="http://www.planets-project.eu/services")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ServiceDescription {

    /**
     * A brief name by which this service is known.
     */
    @XmlElement(name="title", namespace="http://purl.org/dc/terms/", required=true)
    String name;

    /**
     * The name of the concrete implementation class.
     */
    String classname;
    
    /**
     * The type of the service, which is the fully qualified name of the service interface.
     */
    String type;
    
    /**
     * Declared Parameters: [name, type, value (default)]*n
     */
    @XmlElement()
    Parameters parameters;
    
    /**
     *  The link to the Tool registry.
     */
    URI tool;

    /**
     * Human readable description of the service.
     * Allow to be HTML, using a <![CDATA[ <b>Hi</b> ]]>
     */
    @XmlElement(name="description", namespace="http://purl.org/dc/terms/")
    String description;

    /**
     * Wrapper version.
     */
    String version;
    
    /**
     * Identifier - A unique identifier for this service.
     *  "We need a unique id for every service; Andrew Lindley is using a MD5 hash to identify a service. This 
     *  is a brilliant idea. I would say this field summarizes Name of class impl service, Version of service 
     *  and ID of Tool (URI) or makes them unnecessary."
     */
    @XmlElement(name="identifier", namespace="http://purl.org/dc/terms/")
    String identifier;
    
    /**
     * Who wrote the wrapper.
     * Preferred form would be a URI or a full email address, like: "Full Name <fullname@server.com>".
     */
    @XmlElement(name="creator", namespace="http://purl.org/dc/terms/")
    String author;
    
    /**
     * The organisation that is publishing this service endpoint.
     */
    @XmlElement(name="publisher", namespace="http://purl.org/dc/terms/")
    String serviceProvider;
    
    /**
     * Installation instructions. Properties to be set, or s/w to be installed. 
     * Allow to be HTML, using non-parsed embedding, like this: <![CDATA[ <b>Hi</b> ]]>. JAXB should handle this.
     */
    String instructions;
    
    /**
     * Link to further information about this service wrapper.
     */
    URI furtherInfo;
    
    /**
     *  Services may specify what types they can take as inputs. [input]*n
     *  This is particularly useful for Validate and Characterise.
     */
    @XmlElement(name = "inputFormat", required = false)
    List<URI> inputFormats;
    
    /**
     * Name-value pairs for extra properties.  
     * This is an expansion point for future functionality.
     */
    @XmlElement(name = "property", required = false)
    List<Property> properties;
    
    /* --------------------------------------------------------------------------------------------- */

    /**
     * 
     */
    public ServiceDescription() {
        super();
    }

    /**
     * @param name
     * @param type
     */
    public ServiceDescription(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }



    /* --------------------------------------------------------------------------------------------- */

    /**
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /**
     * @param classname the classname to set
     */
    public void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the parameters
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the tool
     */
    public URI getTool() {
        return tool;
    }

    /**
     * @param tool the tool to set
     */
    public void setTool(URI tool) {
        this.tool = tool;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * @param instructions the instructions to set
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * @return the furtherInfo
     */
    public URI getFurtherInfo() {
        return furtherInfo;
    }

    /**
     * @param furtherInfo the furtherInfo to set
     */
    public void setFurtherInfo(URI furtherInfo) {
        this.furtherInfo = furtherInfo;
    }

    /**
     * @return the inputFormats
     */
    public List<URI> getInputFormats() {
        return inputFormats;
    }

    /**
     * @param inputFormats the inputFormats to set
     */
    public void setInputFormats(List<URI> inputFormats) {
        this.inputFormats = inputFormats;
    }

    /**
     * @return the properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    
    /* --------------------------------------------------------------------------------------------- */
    
    /* Proposed hashing and equality methods, generated using Eclipse */

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((classname == null) ? 0 : classname.hashCode());
        result = prime * result + ((tool == null) ? 0 : tool.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceDescription other = (ServiceDescription) obj;
        if (classname == null) {
            if (other.classname != null)
                return false;
        } else if (!classname.equals(other.classname))
            return false;
        if (tool == null) {
            if (other.tool != null)
                return false;
        } else if (!tool.equals(other.tool))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }
        
    /* --------------------------------------------------------------------------------------------- */
    
    /**
     * @param xml The XML representation of a service description (as created from
     *        calling toXml)
     * @return A digital object instance created from the given XML
     */
    public static ServiceDescription of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext.newInstance(ServiceDescription.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            ServiceDescription unmarshalled = (ServiceDescription) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return An XML representation of this service description (can be used to
     *         instantiate an object using the static factory method)
     */
    public String toXml( boolean formatted ) {
        // Update the identifier using the hash code
        this.identifier = ""+this.hashCode();
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext.newInstance(ServiceDescription.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setProperty("jaxb.formatted.output", formatted);
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.name + " : " + this.type + " : " + this.getDescription();
    }

    
}
