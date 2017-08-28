package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.UnorderedArray;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by Guillaume Bailleul on 05/05/2017.
 */
public class TestXmlReader {

    private XmlReader reader = new XmlReader();


    private Packet load (String path) throws Exception{
        InputStream is = getClass().getResourceAsStream(path);
        assertNotNull("Test file not found: "+ path,is);
        return reader.parse(is);
    }

    @Test
    public void readEmptyRdfElement () throws Exception {
        Packet packet = load("/empty-no-description.xml");
        assertNull(packet.getTargetResource());
        assertTrue(packet.getProperties().isEmpty());
    }

    @Test
    public void readEmptyRdfDescriptionNoTarget () throws Exception {
        Packet packet = load("/empty-rdf-notarget.xml");
        assertNull(packet.getTargetResource());
        assertTrue(packet.getProperties().isEmpty());
    }

    @Test
    public void readEmptyRdfDescription () throws Exception {
        Packet packet = load("/empty-rdf.xml");
        assertNotNull(packet.getTargetResource());
        assertTrue(packet.getProperties().isEmpty());
    }

    @Test(expected = XMLStreamException.class)
    public void readInvalidDescription () throws Exception {
        load("/empty-invalid-description.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void unexpectedDescription () throws Exception {
       load("/empty-unexpected-description.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void unexpectedRDF () throws Exception {
        load("/empty-unexpected-rdf.xml");
    }

    @Test
    public void simpleXmp () throws Exception {
        Packet packet = load("/xmp-1.xml");
        assertEquals(1,packet.getProperties().size());
    }

    @Test
    public void simpleXmpWithEmptyProperty () throws Exception {
        Packet packet = load("/xmp-1-1-empty-property.xml");
        assertEquals(1,packet.getProperties().size());
 //       packet.getProperties().get(new Name("http://ns.adobe.com/xap/1.0/","BaseURL"))
  //      assertEquals("",((SimpleValue)(packet.getProperties().get(new Name("http://ns.adobe.com/xap/1.0/","BaseURL")))).getContent());
    }

    @Test
    public void simpleXmpInline () throws Exception {
        Packet packet = load("/xmp-1-inline.xml");
        assertEquals(1,packet.getProperties().size());
    }

    @Test
    public void validUnorderedArray () throws Exception {
        Packet packet = load("/xmp-2-array.xml");
        assertEquals(2,packet.getProperties().size());
        assertTrue(packet.getProperties().get(DublinCore.SUBJECT) instanceof UnorderedArray);
    }

    @Test
    public void validUnorderedArrayInline () throws Exception {
        Packet packet = load("/xmp-2-array-inline.xml");
        assertEquals(2,packet.getProperties().size());
        assertTrue(packet.getProperties().get(DublinCore.SUBJECT) instanceof UnorderedArray);
    }

    @Test
    public void rdfDescriptionInArray () throws Exception {
        Packet packet = load("/xmp-3-array-description.xml");
        assertEquals(1,packet.getProperties().size());
        assertTrue(packet.getProperties().get(DublinCore.SUBJECT) instanceof UnorderedArray);
    }

    @Test
    public void rdfDescriptionInSimple () throws Exception {
        Packet packet = load("/xmp-4-simple-description.xml");
        assertEquals(1,packet.getProperties().size());
//        assertTrue(packet.getProperties().get(DublinCore.SOURCE) instanceof UnorderedArray);
    }

    @Test
    public void rdfLangAltInSimple () throws Exception {
        Packet packet = load("/xmp-5-simple-langalt.xml");
        assertEquals(1,packet.getProperties().size());
//        assertTrue(packet.getProperties().get(DublinCore.SUBJECT) instanceof UnorderedArray);
    }

    @Test
    public void descriptionWithNoTagInProperty () throws Exception {
        Packet packet = load("/xmp-6-array-in-property.xml");
        assertEquals(1,packet.getProperties().size());
        // TODO drill down in structure
    }
}
