package eu.planets_project.ifr.core.services.comparison.comparator.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.XcdlProperties;
import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.utils.FileUtils;

/**
 * Access to CPR (the XCDL comparator result format) properties.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ResultPropertiesReader {

    private static final Namespace NS = Namespace.getNamespace("http://www.planets-project.eu/xcl/schemas/xcl");

    private File cprFile;

    /**
     * @param cprFile The CPR comparator result file
     */
    public ResultPropertiesReader(final File cprFile) {
        this.cprFile = fix(cprFile);
    }

    /**
     * FIXME temporary workaround for a bug in the 1.0 comparator beta.
     * @param file The file to fix
     * @return The fixed file
     */
    private File fix(final File file) {
        String content = FileUtils.readTxtFileIntoString(file);
        Scanner s = new Scanner(content);
        boolean closedProperty = false;
        StringBuilder builder = new StringBuilder();
        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            if (!(line.equals("</property>") && closedProperty)) {
                builder.append(line).append("\n");
            }
            if (line.startsWith("<property") && line.endsWith("/>")) {
                closedProperty = true;
            } else {
                closedProperty = false;
            }
        }
        return FileUtils.writeStringToFile(builder.toString(), file);
    }

    /**
     * @return The properties in the given CPR file
     */
    public List<Property> getProperties() {
        List<Property> properties = new ArrayList<Property>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(cprFile);
            Element obj = doc.getRootElement().getChild("set", NS);
            if (obj == null) {
                String childText = doc.getRootElement().getChildText("error", NS);
                throw new IllegalArgumentException("Can't process document: " + childText);
            }
            List<?> propElems = obj.getChildren("property", NS);
            for (Object object : propElems) {
                Element e = (Element) object;
                String state = e.getAttributeValue("state");
                String description = state.equals("complete") ? processMetrics(e) : "";
                String name = e.getAttributeValue("name");
                String desc = "[" + description + "]";
                Property result = new Property.Builder(XcdlProperties.makePropertyURI(name)).name(name).value(state)
                        .description(desc).build();
                properties.add(result);
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private String processMetrics(final Element propertyElement) {
        StringBuilder descriptionBuilder = new StringBuilder();
        Element metricsElem = propertyElement.getChild("metrics", NS);
        if (metricsElem != null) {
            List<?> metrics = metricsElem.getChildren();
            for (Object mObject : metrics) {
                Element m = (Element) mObject;
                String metricName = m.getAttributeValue("name");
                String resultString = m.getChildText("result", NS);
                descriptionBuilder.append(String.format(" %s=%s", metricName, resultString));
            }
        }
        return descriptionBuilder.toString().trim();
    }
}
