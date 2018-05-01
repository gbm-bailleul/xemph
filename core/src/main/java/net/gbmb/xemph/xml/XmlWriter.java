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
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.values.*;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Class used to convert a packet in an xml stream
 */
public class XmlWriter {

    private static final String DEFAULT_ABOUT = "";

    private boolean simpleValueAsAttribute = false;

    private boolean groupRdfDescription = true;

    private boolean isIndent = false;

    public boolean isGroupRdfDescription() {
        return groupRdfDescription;
    }

    public void setGroupRdfDescription(boolean groupRdfDescription) {
        this.groupRdfDescription = groupRdfDescription;
    }

    public boolean isSimpleValueAsAttribute() {
        return simpleValueAsAttribute;
    }

    public void setSimpleValueAsAttribute(boolean simpleValueAsAttribute) {
        this.simpleValueAsAttribute = simpleValueAsAttribute;
    }

    public boolean isIndent() {
        return isIndent;
    }

    public void setIsIndent(boolean indent) {
        this.isIndent = indent;
    }

    public void write (Packet packet, OutputStream output) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty("javax.xml.stream.isRepairingNamespaces",true);
            XMLStreamWriter writer = factory.createXMLStreamWriter(output);
            // start document
            writer.writeStartDocument();
            // write content
            writePacket(packet,writer);
            // end document
            writer.writeEndDocument();
            writer.flush();
            writer.close();

        } catch (XMLStreamException e) {
            throw new IOException("Failed to write output",e);
        }
    }

    public void writePacket (Packet packet, XMLStreamWriter writer) throws XMLStreamException {
        // start document
        writer.writeStartElement(Namespaces.RDF,"RDF");
        writer.setPrefix("rdf", Namespaces.RDF);
        writer.writeNamespace("rdf",Namespaces.RDF);
        // write content
        writeContent (packet, writer);
        // end
        writer.writeEndElement();
    }

    private void writeContent(Packet packet, XMLStreamWriter writer)  throws XMLStreamException {
        // add namespaces definition
        Namespaces namespaces = packet.getNamespaces();
        for (String ns: packet.listUsedNamespaces()) {
            writer.writeNamespace(namespaces.getPrefixFor(ns),ns);
        }
        if (groupRdfDescription) {
            // all properties will be in same rdf:Description
            writer.writeStartElement(Namespaces.RDF,"Description");
            writer.writeAttribute(Namespaces.RDF,"about",DEFAULT_ABOUT);
        }
        for (Name name: packet.getPropertiesNames()) {
            if (!groupRdfDescription) {
                // one property description for each property
                writer.writeStartElement(Namespaces.RDF,"Description");
                writer.writeAttribute(Namespaces.RDF,"about",DEFAULT_ABOUT);
            }
            // the value
            Value value = packet.getValue(name);
            if (value instanceof SimpleValue) {
                writeSimpleValue(name, (SimpleValue) value, writer);
            } else if (value instanceof Structure) {
                writeStructureValue(name, (Structure) value, writer);
            } else if (value instanceof ArrayValue) {
                writeArray(name,(ArrayValue<? extends Value>)value,writer);
            } else {
                throw new IllegalArgumentException("Unable to serialize type "+value.getClass().getSimpleName());
            }
            // closing description if needed
            if (!groupRdfDescription) {
                writer.writeEndElement();
            }
        }
        if (groupRdfDescription) {
            writer.writeEndElement();
        }
    }

    private void writeSimpleValue (Name name,SimpleValue value, XMLStreamWriter writer) throws XMLStreamException {
        if (value.hasQualifiers()) {
            writeSimpleValueWithQualifiers(name,value,writer);
        } else {
            // write simple value
            if (simpleValueAsAttribute) {
                // write value as attribute when possible
                writer.writeEmptyElement(name.getNamespace(),name.getLocalName());
                writer.writeAttribute(Namespaces.RDF,"resource",value.getContent());
            } else {
                // simple value as element
                writer.writeStartElement(name.getNamespace(),name.getLocalName());
                writer.writeCharacters(value.getContent());
                writer.writeEndElement();
            }
        }
    }

    private void writeSimpleValueWithQualifiers(Name name, SimpleValue value, XMLStreamWriter writer) throws XMLStreamException{
        writer.writeStartElement(name.getNamespace(),name.getLocalName());
        writer.writeStartElement(Namespaces.RDF,"Description");
        // the value
        writer.writeStartElement(Namespaces.RDF,"value");
        writer.writeCharacters(value.getContent());
        writer.writeEndElement();
        // the qualifiers
        for (Map.Entry<Name,String> entry: value.getQualifiers().entrySet()) {
            writer.writeStartElement(entry.getKey().getNamespace(),entry.getKey().getLocalName());
            writer.writeCharacters(entry.getValue());
            writer.writeEndElement();
        }
        // closing
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeStructureValue(Name name, Structure value, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(name.getNamespace(),name.getLocalName());
        writer.writeStartElement(Namespaces.RDF,"Description");
        for (Name key: value.keySet()) {
            writer.writeStartElement(key.getNamespace(),key.getLocalName());
            writer.writeCharacters(((SimpleValue)value.getField(key)).getContent());
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeArray (Name name, ArrayValue<? extends Value> array, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(name.getNamespace(),name.getLocalName());
        if (array instanceof OrderedArray) {
            writer.writeStartElement(Namespaces.RDF,"Seq");
        } else if (array instanceof UnorderedArray) {
            writer.writeStartElement(Namespaces.RDF,"Bag");
        } else if (array instanceof AlternativeArray) {
            writer.writeStartElement(Namespaces.RDF,"Alt");
        }
        for (Value value: array.getItems()) {
            writer.writeStartElement(Namespaces.RDF,"li");
            // add xml:lang qualifier if existing
            if (value.getXmlLang()!=null) {
                writer.writeAttribute(Namespaces.XML,"lang",value.getXmlLang());
            }
            if (!value.hasQualifiers()) {
                writer.writeCharacters(((SimpleValue)value).getContent());
            } else {
                // display qualifiers
                writer.writeStartElement(Namespaces.RDF,"Description");
                writer.writeStartElement(Namespaces.RDF,"Value");
                writer.writeCharacters(((SimpleValue)value).getContent());
                writer.writeEndElement();
                // the qualifiers
                for (Map.Entry<Name,String> entry: value.getQualifiers().entrySet()) {
                    writer.writeStartElement(entry.getKey().getNamespace(),entry.getKey().getLocalName());
                    writer.writeCharacters(entry.getValue());
                    writer.writeEndElement();
                }
                // closing
                writer.writeEndElement();
            }
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }


}
