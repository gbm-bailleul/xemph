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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

/**
 * Helper class for xmp parsing.
 */
public class XmpReader {


    private XmlReader xmlReader;

    public XmpReader (XmlReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    public XmpReader () {
        this(new XmlReader());
    }

    public Packet parse (InputStream input) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader reader = factory.createXMLEventReader(input);
        // read the xpacket
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (!event.isStartElement())
                continue;
            StartElement se = event.asStartElement();
            if (Name.Q.XMP_META.equals(se.getName())) {
                return xmlReader.parse(reader);
            }
        }
        // failed to find xmpmeta
        throw new XMLStreamException("Stream looks invalid xmpmeta");
    }

}
