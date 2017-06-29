package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.*;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 21/10/2016.
 */
public class XmlReader {

    public Packet parse(InputStream input) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_COALESCING,true);
        XMLEventReader reader = factory.createXMLEventReader(input);
        return parse(reader);
    }

    public Packet parse(XMLEventReader reader) throws XMLStreamException {
        Packet packet = new Packet();
        // go to first startElement
        while (reader.hasNext() && !reader.peek().isStartElement())
            reader.nextEvent();
        // TODO ensure we are on rdf:RDF
        // start parsing expecting rdf:RDF
        StartElement se = reader.nextTag().asStartElement();
        if (!se.getName().equals(Name.Q.RDF_RDF))
            throw new XMLStreamException(String.format("Expected %s at %d:%d(%d)",Name.Q.RDF_RDF,se.getLocation().getLineNumber(),se.getLocation().getColumnNumber(),se.getLocation().getCharacterOffset()));
        loadNamespaces(se, packet);
        // now parsing description
        XMLEvent event = reader.nextTag();
        while (event.isStartElement()) {
            if (Name.Q.RDF_DESCRIPTION.equals(event.asStartElement().getName())) {
                // enters in Description if element if rdf:Description
                parseDescription(event.asStartElement(),reader,packet);
            } else {
                // we only expect rdf:Description here
                throw new XMLStreamException("Only expecting start element: "+Name.Q.RDF_DESCRIPTION);
            }
            event = reader.nextTag();
        }
        return packet;
    }

    private void parseTarget(StartElement se, Packet packet) {
        Attribute about = se.getAttributeByName(new QName(Namespaces.RDF, "about"));
        if (about != null && about.getValue() != null && about.getValue().length() > 0) {
            // only set target if not empty (and not null)
            packet.setTargetResource(about.getValue());
        }
    }

    private void parseDescription (StartElement se, XMLEventReader reader, Packet packet) throws XMLStreamException {
        parseTarget(se,packet);
        XMLEvent event = reader.nextTag();
        while (event.isStartElement()) {
            // this is a new property in the description
            parseProperty(event.asStartElement(),reader,packet);
            event = reader.nextTag();
        }
        // should be the description end
        if (!event.isEndElement() || !event.asEndElement().getName().equals(Name.Q.RDF_DESCRIPTION))
            throw forge("Expecting closing rdf:Description and found: "+event,event.getLocation());
    }

    private void parseProperty(StartElement se, XMLEventReader reader, Packet packet) throws XMLStreamException {
        loadNamespaces(se, packet);
        String ns = se.getName().getNamespaceURI();
        String ln = se.getName().getLocalPart();
        XMLEvent next = reader.nextEvent();
        if (next.isCharacters() && reader.peek().isStartElement()) {
            // this is a whitespace
            next = reader.nextEvent();
        }
        if (next.isCharacters()) {
            // simple value
            SimpleValue value = new SimpleValue(next.asCharacters().getData());
            packet.addProperty(new Name(ns, ln), value);
        } else if (next.isStartElement()) {
            StartElement sde = (StartElement) next;
            if (Namespaces.RDF.equals(sde.getName().getNamespaceURI())) {
                switch (sde.getName().getLocalPart()) {
                    case "Description":
                        packet.addProperty(new Name(se.getName()), parseDescription(reader));
                        reader.nextTag();
                        break;
                    case "Bag":
                    case "Seq":
                    case "Alt":
                        packet.addProperty(new Name(se.getName()), parseArray(sde, reader));
                        reader.nextTag();
                        break;
                    default:
                        throw new XMLStreamException("Unknown description element: " + sde.getName());
                }
            } else {
                throw new XMLStreamException("Unknown description namespace: " + sde.getName());
            }
        } else {
            throw forge("Unexpected item: "+next,next.getLocation());
        }
        // ending property description
        reader.next();
    }


    private Value parseDescription(XMLEventReader reader) throws XMLStreamException {
        return parseDescription(reader,null);
    }

    private Value parseDescription(XMLEventReader reader, XMLEvent current) throws XMLStreamException {
        XMLEvent next = current!=null?current:reader.nextTag();
        Map<QName, Value> found = new HashMap<>();
        while (next.isStartElement()) {
            QName qn = next.asStartElement().getName();
            XMLEvent valueEvent = reader.nextEvent();
            if (valueEvent.isCharacters() && reader.peek().isStartElement()) {
                // whitespace
                valueEvent = reader.nextEvent();
            }
            if (valueEvent.isCharacters()) {
                found.put(qn, new SimpleValue(valueEvent.asCharacters().getData()));
                reader.nextTag(); // closing tag
                next = reader.nextTag();
            } else if (valueEvent.isStartElement()) {
                if (Namespaces.RDF.equals(valueEvent.asStartElement().getName().getNamespaceURI())) {
                    switch (valueEvent.asStartElement().getName().getLocalPart()) {
//                        case "Description":
//                            packet.addProperty(new Name(se.getName()), parseDescription(reader));
//                            reader.nextTag();
//                            break;
                        case "Bag":
                        case "Seq":
                        case "Alt":
                            return parseArray(valueEvent.asStartElement(), reader);
                        default:
                            throw forge("Unknown description element: " + valueEvent.asStartElement().getName(),valueEvent.getLocation());
                    }
                }  else {
                    throw forge("Not implemented 2: "+valueEvent,valueEvent.getLocation());
                }
            } else {
                throw forge("Not implemented: "+valueEvent,valueEvent.getLocation());
            }
        }
        if (found.containsKey(Name.Q.XML_LANG)) {
            // description of a value
            Value value = found.remove(Name.Q.XML_LANG);
            found.entrySet().forEach(e -> value.addQualifier(new Name(e.getKey()), ((SimpleValue)e.getValue()).getContent()));
            return value;
        } else {
            // description of structure if all namespaces are equals
            Structure struct = new Structure();
            found.entrySet().forEach(e -> struct.add(new Name(e.getKey()), e.getValue()));
            return struct;
        }
    }


    private ArrayValue parseArray(StartElement se, XMLEventReader reader) throws XMLStreamException {
        ArrayValue<Value> array;
        if ("Seq".equals(se.getName().getLocalPart()))
            array = new OrderedArray<>();
        else if ("Bag".equals(se.getName().getLocalPart()))
            array = new UnorderedArray<>();
        else if ("Alt".equals(se.getName().getLocalPart()))
            array = new AlternativeArray<>();
        else
            throw new XMLStreamException("Unknown array type: " + se.getName());
        // read the list
        XMLEvent next = reader.nextTag();
        while (next.isStartElement() && "li".equals(((StartElement) next).getName().getLocalPart())) {
            XMLEvent valueEvent = reader.nextEvent();
            if (valueEvent.isCharacters() && reader.peek().isStartElement()) {
                // whitespace to skip
                valueEvent = reader.nextTag();
            }
            if (valueEvent.isCharacters()) {
                SimpleValue value = new SimpleValue(valueEvent.asCharacters().getData());
                // check if lang is present
                Attribute attr = ((StartElement) next).getAttributeByName(Name.Q.XML_LANG);
                if (attr != null) {
                    value.setXmlLang(attr.getValue());
                }
                array.addItem(value);
            } else if (valueEvent.isStartElement()) {
                if (Name.Q.RDF_DESCRIPTION.equals(valueEvent.asStartElement().getName())) {
                    // description in li
                    Value value = parseDescription(reader);
                    array.addItem(value);
//                    reader.nextTag();
                } else {
                    Value value = parseDescription(reader,valueEvent);
                    array.addItem(value);
//                    throw forge("Case not handled: "+valueEvent,valueEvent.getLocation());
                }
            } else {
                throw forge("Unexpected event in li: "+valueEvent,valueEvent.getLocation());
            }
            next = reader.nextTag();
            next = reader.nextTag();
        }
//        reader.nextTag();
        return array;
    }


    private void loadNamespaces(StartElement se, Packet packet) {
        // load all namespaces
        Iterator<Namespace> namespaces = se.getNamespaces();
        while (namespaces.hasNext()) {
            Namespace namespace = namespaces.next();
            packet.getNamespaces().registerNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
        }
    }

    private XMLStreamException forge (String message, Location location) {
        return new XMLStreamException(String.format("%s at %d:%d(%d)",
                message,
                location.getLineNumber(),
                location.getColumnNumber(),
                location.getCharacterOffset()));
    }

}
