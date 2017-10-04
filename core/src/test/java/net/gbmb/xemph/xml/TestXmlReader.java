/*
 * Copyright 2017 Guillaume Bailleul.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.values.OrderedArray;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.Structure;
import net.gbmb.xemph.values.UnorderedArray;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.*;

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

    @Test(expected = XMLStreamException.class)
    public void simpleXmpWithUnexpectedEntity () throws Exception {
        load("/xmp-1-2-unexpected-entity.xml");
    }

    @Test
    public void simpleXmpWithEmptyProperty () throws Exception {
        Packet packet = load("/xmp-1-1-empty-property.xml");
        assertEquals(1,packet.getProperties().size());
        assertEquals("",((SimpleValue)(packet.getProperties().get(new Name("http://ns.adobe.com/xap/1.0/","BaseURL")))).getContent());
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

    @Test(expected = XMLStreamException.class)
    public void invalidArrayType () throws Exception {
        load("/xmp-2-2-not-existing-list.xml");
    }

    @Test(expected = XMLStreamException.class)
    public void invalidArrayNamespaceType () throws Exception {
        load("/xmp-2-3-not-existing-list-ns.xml");
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
        Map.Entry<Name,Value> entry = packet.getProperties().entrySet().iterator().next();
        assertEquals(new Name("http://www.aiim.org/pdfa/ns/extension/","schemas"),entry.getKey());
        assertTrue (entry.getValue() instanceof UnorderedArray);
        UnorderedArray schemas = (UnorderedArray)entry.getValue();
        assertEquals(1,schemas.size());
        assertTrue(schemas.getItems().get(0) instanceof Structure);
        Structure schemaElement = schemas.getItemAsStructure(0);
        assertEquals(4, schemaElement.size());
        OrderedArray<Structure> property = (OrderedArray)schemaElement.getField(new Name("http://www.aiim.org/pdfa/ns/schema#","property"));
        Structure first = property.getItem(0);
        assertEquals(4,first.size());
        assertEquals(new SimpleValue("Text"),first.getField(new Name("http://www.aiim.org/pdfa/ns/property#","valueType")));
        assertEquals(new SimpleValue("internal"),first.getField(new Name("http://www.aiim.org/pdfa/ns/property#","category")));
        assertEquals(new SimpleValue("Trapped"),first.getField(new Name("http://www.aiim.org/pdfa/ns/property#","name")));
    }

    @Test
    public void arrayOfStruct () throws Exception {
        Packet packet = load("/xmp-7-2-array-of-struct.xml");
        assertEquals(1,packet.getProperties().size());
        Name name = packet.getProperties().keySet().iterator().next();
        Value value = packet.getValue(name);
        assertTrue(value instanceof UnorderedArray);

        UnorderedArray array = (UnorderedArray)value;
        assertEquals(2,array.size());
        assertTrue(array.getItems().get(0) instanceof Structure);
        assertTrue(array.getItems().get(1) instanceof Structure);

        Structure struct = (Structure)array.getItems().get(0);
        assertEquals(3,struct.size());
        System.out.println(array.getItems().get(0));
    }

    @Test
    public void simpleStruct () throws Exception {
        Packet packet = load("/xmp-7-1-struct.xml");
        assertEquals(1,packet.getProperties().size());
        Name name = packet.getProperties().keySet().iterator().next();
        Value value = packet.getValue(name);
        assertTrue(value instanceof Structure);

        Structure struct = (Structure)value;
        assertEquals(3,struct.size());
        System.out.println(value);
    }
}
