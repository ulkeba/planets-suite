package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.planets_project.services.datatypes.Property;

/**
 * Tests and sample usage for the XcdlCreator.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public class XcdlCreatorTests {
    private static XcdlCreator creator;
    private static String xcdlXml;

    @BeforeClass
    public static void setup() {
        /*
         * TODO: Does this make any sense? Probably not so much from an API perspective, but maybe when such properties
         * are generated... Can we unclutter this? Should we use enums for the string values? Should we get them from
         * the annotations of the generated classes, e.g. PropertySet.class.getAnnotation(XmlRootElement.class).name();?
         */
        List<Property> norm = Arrays.asList(
        /* Norm data: */
        new Property.Builder(XcdlProperties.makePropertyURI("normData")).name("normData").type("normData").description(
                "image").value("00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 11 12 13 14 15").build());
        /* Property sets: */
        List<Property> sets = Arrays.asList(new Property.Builder(XcdlProperties.makePropertyURI("propertySet")).name(
                "propertySet").type("propertySet").description(
                "ref i_i1_i217_s4 suggestedPaletteAlpha, ref i_i1_i217_s5 suggestedPaletteFrequency").build());
        /* Properties: */
        List<Property> properties = Arrays.asList(new Property.Builder(XcdlProperties.makePropertyURI("property"))
                .name("property").type("property").description(
                        "raw descr, name id57 suggestedPaletteFrequency, "
                                + "valueSet i_i1_i217_s5, labValue 0 int inch, dataRef id_1 global").build(),
                new Property.Builder(XcdlProperties.makePropertyURI("property")).name("property").type("property")
                        .description(
                                "raw descr, name id56 suggestedPaletteAlpha, "
                                        + "valueSet i_i1_i217_s4, labValue 255 int inch, dataRef id_1 global").build());
        List<Property> all = new ArrayList<Property>();
        all.addAll(norm);
        all.addAll(sets);
        all.addAll(properties);
        List<List<Property>> result = new ArrayList<List<Property>>();
        result.add(all);
        creator = new XcdlCreator(result);
        xcdlXml = creator.getXcdlXml();
    }

    @Test
    public void testSerialiaztion() {
        Assert.assertNotNull("Could not serialize the XCDL object tree", xcdlXml);
        System.out.println(xcdlXml);

    }

    @Test
    public void testNormData() {
        Assert.assertTrue("Norm data element not present in serialized form", xcdlXml.contains("<normData"));
    }

    @Test
    public void testProperties() {
        Assert.assertTrue("Property element not present in serialized form", xcdlXml.contains("<property"));
    }

    @Test
    public void testPropertySets() {
        Assert.assertTrue("Property set element not present in serialized form", xcdlXml.contains("<propertySet"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFaultyProp() {
        List<Property> list = Arrays.asList(new Property.Builder(XcdlProperties.makePropertyURI("totally")).name(
                "random").value("property").build());
        List<List<Property>> result = new ArrayList<List<Property>>();
        result.add(list);
        new XcdlCreator(result);
    }
}
