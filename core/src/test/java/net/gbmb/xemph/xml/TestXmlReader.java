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
    public void simpleXmpInline () throws Exception {
        Packet packet = load("/xmp-1-inline.xml");
        assertEquals(1,packet.getProperties().size());
    }

    @Test
    public void validUnorderedArrayInline () throws Exception {
        Packet packet = load("/xmp-2-array-inline.xml");
        assertEquals(2,packet.getProperties().size());
        assertTrue(packet.getProperties().get(DublinCore.SUBJECT) instanceof UnorderedArray);
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



}
