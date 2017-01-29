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
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class XmlWriter {

    private boolean simpleValueAsAttribute = false;

    private boolean groupRdfDescription = true;

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

    public void write (Packet packet, OutputStream output) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(output);
            // start document
            writer.writeStartDocument();
            writer.setPrefix("rdf", Namespaces.RDF);
            writer.writeStartElement(Namespaces.RDF,"RDF");
            writer.writeNamespace("rdf",Namespaces.RDF);
            // write content
            writeContent (packet, writer);
            // end document
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();

        } catch (XMLStreamException e) {
            throw new IOException("Failed to write output",e);
        }
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
            writer.writeAttribute(Namespaces.RDF,"about",""); // TODO where to find about value
        }
        for (Name name: packet.getPropertiesNames()) {
            if (!groupRdfDescription) {
                // one property description for each property
                writer.writeStartElement(Namespaces.RDF,"Description");
                writer.writeAttribute(Namespaces.RDF,"about",""); // TODO where to find about value
            }
            // the value
            Value value = packet.getProperty(name);
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
            // write very simple value
            // TODO handle xml markup in value (7.5 example 3)
            if (simpleValueAsAttribute) {
                // write value as attribute when possible
                writer.writeEmptyElement(name.getNamespace(),name.getLocalName());
                writer.writeAttribute(Namespaces.RDF,"resource",value.getContent());
            } else {
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
        for (Map.Entry<Name,Value> entry: value.getFields().entrySet()) {
            writer.writeStartElement(entry.getKey().getNamespace(),entry.getKey().getLocalName());
            writer.writeCharacters(((SimpleValue)entry.getValue()).getContent());
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
                writer.writeCharacters(((SimpleValue)value).getContent()); // TODO other types than simple text
            } else {
                // display qualifiers
                writer.writeStartElement(Namespaces.RDF,"Description");
                writer.writeStartElement(Namespaces.RDF,"Value");
                writer.writeCharacters(((SimpleValue)value).getContent()); // TODO other types than simple text
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
