package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.*;

import javax.xml.namespace.QName;
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

    private enum State {OUT,RDF,DESCRIPTION,PROPERTY}

    public Packet parse (InputStream input) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader reader = factory.createXMLEventReader(input);
        State readState = State.OUT;
        Packet packet = new Packet();
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement se = event.asStartElement();
                if (readState==State.OUT && Name.Q.RDF_RDF.equals(se.getName())) {
                    // we enter RDF if element is rdf:RDF
                    readState = State.RDF;
                    loadNamespaces(se,packet);
                } else if (readState==State.RDF && Name.Q.RDF_DESCRIPTION.equals(se.getName())) {
                    // enters in Description if element if rdf:Description
                    readState = State.DESCRIPTION;
                    Attribute about = se.getAttributeByName(QName.valueOf("rdf:about"));
                    if (about!=null) {
                        packet.setTargetResource(about.getValue());
                    }
                } else {
                    // should be property description
                    parseProperty (se,reader,packet);
                }
            }
        }


        return packet;
    }

    private void parseProperty(StartElement se, XMLEventReader reader, Packet packet) throws XMLStreamException {
        loadNamespaces(se,packet);
        String ns = se.getName().getNamespaceURI();
        String ln = se.getName().getLocalPart();
        XMLEvent next = nextToUse(reader);
        if (next.isCharacters()) {
            // simple value
            SimpleValue value = new SimpleValue(next.asCharacters().getData());
            packet.addProperty(new Name(ns,ln),value);
        } else if (next.isStartElement()) {
            StartElement sde = (StartElement)next;
            if (Namespaces.RDF.equals(sde.getName().getNamespaceURI())) {
                switch (sde.getName().getLocalPart()) {
                    case "Bag":
                    case "Seq":
                    case "Alt":
                        packet.addProperty(new Name(se.getName()),parseArray (sde,reader));
                        break;
                    case "Description":
                        packet.addProperty(new Name(se.getName()),parseDescription (reader));
                        break;
                    default:
                        throw new XMLStreamException("Unknown description element: "+sde.getName());
                }
            } else {
                throw new XMLStreamException("Unknown description namespace: "+sde.getName());
            }
        } else {
            throw new XMLStreamException("Unexpected item: "+next);
        }


        // ending property description
        reader.next();
    }

    private Value parseDescription (XMLEventReader reader) throws XMLStreamException {
        XMLEvent next  = reader.nextTag();
        Map<QName,String> found = new HashMap<>();
        while (next.isStartElement()) {
            QName qn = next.asStartElement().getName();
            found.put(qn,reader.nextEvent().asCharacters().getData());
            reader.nextTag();
            next = reader.nextTag();
        }
        if (found.containsKey(Name.Q.XML_LANG)) {
            // description of a value
            SimpleValue value = new SimpleValue(found.remove(Name.Q.XML_LANG));
            found.entrySet().forEach( e -> value.addQualifier(new Name(e.getKey()),e.getValue()));
            return value;
        } else {
            // description of structure if all namespaces are equals
            Structure struct = new Structure();
            found.entrySet().forEach( e -> struct.add(new Name(e.getKey()),new SimpleValue(e.getValue())));
            return struct;
        }
    }


    private ArrayValue parseArray (StartElement se, XMLEventReader reader) throws XMLStreamException {
        ArrayValue<Value> array;
        if ("Seq".equals(se.getName().getLocalPart())) array = new OrderedArray<>();
        else if ("Bag".equals(se.getName().getLocalPart())) array = new UnorderedArray<>();
        else if ("Alt".equals(se.getName().getLocalPart())) array = new AlternativeArray();
        else throw new XMLStreamException("Unknown array type: "+se.getName());
        // read the list
        XMLEvent next = nextToUse(reader);
        while (next.isStartElement() && "li".equals(((StartElement)next).getName().getLocalPart())) {
            XMLEvent valueEvent = reader.nextEvent();
            if (valueEvent.isCharacters() && valueEvent.asCharacters().isWhiteSpace()) {
                // skip that element
                valueEvent = reader.nextTag();
            }
            if (valueEvent.isCharacters()) {
                SimpleValue value = new SimpleValue (valueEvent.asCharacters().getData());
                array.addItem(value);
                // check if lang is present
                Attribute attr = ((StartElement) next).getAttributeByName(Name.Q.XML_LANG);
                if (attr!=null) {
                    value.setXmlLang(attr.getValue());
                }
            } else if (valueEvent.isStartElement()) {
                // description in li
                Value value = parseDescription(reader);
                array.addItem(value);
            }
            reader.nextTag();
            next = reader.nextTag();
        }
        reader.nextTag();
        return array;
    }


    private XMLEvent nextToUse (XMLEventReader reader) throws XMLStreamException{
        XMLEvent next = reader.nextEvent();
        if (reader.peek().isStartElement()) {
            // skip characters between two start elements
            return reader.nextEvent();
        } else {
            return next;
        }
    }

    private void loadNamespaces (StartElement se, Packet packet) {
        // load all namespaces
        Iterator<Namespace> namespaces = se.getNamespaces();
        while (namespaces.hasNext()) {
            Namespace namespace = namespaces.next();
            packet.getNamespaces().registerNamespace(namespace.getPrefix(),namespace.getNamespaceURI());
        }
    }

}
