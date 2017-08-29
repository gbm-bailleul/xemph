package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
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

    private Log logger = LogFactory.getLog(this.getClass());

    public XmlReader () {
        log("Starting XmlReader");
    }

    public Packet parse(InputStream input) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_COALESCING,true);
        XMLEventReader reader = factory.createXMLEventReader(input);
        return parse(reader);
    }

    public Packet parse(XMLEventReader reader) throws XMLStreamException {
        Packet packet = new Packet();
        // go to first startElement
        log("Start skipping before first start element");
        while (reader.hasNext() && !reader.peek().isStartElement())
            reader.nextEvent();
        // start parsing expecting rdf:RDF
        StartElement se = reader.nextTag().asStartElement();
        if (!se.getName().equals(Name.Q.RDF_RDF))
            throw new XMLStreamException(String.format("Expected %s at %d:%d(%d)",Name.Q.RDF_RDF,se.getLocation().getLineNumber(),se.getLocation().getColumnNumber(),se.getLocation().getCharacterOffset()));
        log("Found starting rdf:RDF");
        loadNamespaces(se, packet);
        // now parsing description
        XMLEvent event = reader.nextTag();
        while (event.isStartElement()) {
            if (Name.Q.RDF_DESCRIPTION.equals(event.asStartElement().getName())) {
                // enters in Description if element if rdf:Description
                parseCompleteDescription(event.asStartElement(),reader,packet);
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

    // TODO inverser l'ordre des params (consistency)
    private void log (XMLEvent se, String comment) {
        logger.debug(comment+" : "+se.getLocation().getLineNumber()+":"+se.getLocation().getColumnNumber()+" / "+se.toString());
    }

    private void log (String comment) {
        logger.debug(comment);
    }

    private void parseCompleteDescription(StartElement se, XMLEventReader reader, Packet packet) throws XMLStreamException {
        log (se,"IN parseCompleteDescription");
        log(reader.peek(),"Next event");
        parseTarget(se,packet);
        XMLEvent event = reader.nextTag();
        while (event.isStartElement()) {
            // this is a new property in the description
            parseProperty(event.asStartElement(),reader,packet);
            log(reader.peek(),"Event after parseProperty");
            event = reader.nextTag();
        }
        // should be the description end
        if (!event.isEndElement() || !event.asEndElement().getName().equals(Name.Q.RDF_DESCRIPTION))
            throw forge("Expecting closing rdf:Description and found: "+event,event.getLocation());
        log(reader.peek(),"Event after parseCompleteDescription");
    }

    private void parseProperty(StartElement se, XMLEventReader reader, Packet packet) throws XMLStreamException {
        log (se,"IN parseProperty");
        loadNamespaces(se, packet);
        String ns = se.getName().getNamespaceURI();
        String ln = se.getName().getLocalPart();
        XMLEvent next = reader.nextEvent();
        if (next.isCharacters() && next.asCharacters().isWhiteSpace()) {
            // this is a whitespace
            next = reader.nextEvent();
        }
        if (next.isCharacters()) {
            // simple value
            SimpleValue value = new SimpleValue(next.asCharacters().getData());
            packet.addProperty(new Name(ns, ln), value);
            reader.nextTag();
        } else if (next.isEndElement()) {
            // the value was empty, consider simple value with empty string
            SimpleValue value = new SimpleValue("");
            packet.addProperty(new Name(ns,ln), value);
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
                Structure struct =parseStructure(sde,reader);
                packet.addProperty(new Name(se.getName()),struct);
            }
        } else {
            throw forge("Unexpected item: "+next,next.getLocation());
        }
        // ending property description
        log(reader.peek(),"End parseProperty");
    }


    private Value parseDescription(XMLEventReader reader) throws XMLStreamException {
        Value value =  parseDescriptionContent(reader);
        XMLEvent event = reader.nextTag();
        if (!event.isEndElement())
            throw forge("Expecting end of description tag, found: "+event,event.getLocation());
        return value;
    }

    /**
     * Parse the content of a description
     * The reader.nextEvent() must return the first tag of the description
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    private Value parseDescriptionContent(XMLEventReader reader) throws XMLStreamException {
        Map<QName, Value> found = new HashMap<>();
        XMLEvent next = peekNextNonIgnorable(reader);
        while (next.isStartElement()) {
            next = reader.nextEvent();
            QName qn = next.asStartElement().getName();
            XMLEvent valueEvent = peekNextNonIgnorable(reader);

            if (valueEvent.isCharacters()) {
                found.put(qn, new SimpleValue(valueEvent.asCharacters().getData()));
                reader.nextEvent(); // the content that was only peek
                reader.nextTag(); // closing tag
                next = peekNextNonIgnorable(reader);
            } else if (valueEvent.isStartElement()) {
                if (Namespaces.RDF.equals(valueEvent.asStartElement().getName().getNamespaceURI())) {
                    switch (valueEvent.asStartElement().getName().getLocalPart()) {
                        case "Description":
                            // TODO should test this grammar case
                            found.put(qn,parseDescription(reader));
                            reader.nextTag();
                            next = peekNextNonIgnorable(reader);
                            break;
                        case "Bag":
                        case "Seq":
                        case "Alt":
                            valueEvent = reader.nextTag();
                            Value value =  parseArray(valueEvent.asStartElement(), reader);
                            found.put(qn,value);
                            reader.nextTag();
                            next = peekNextNonIgnorable(reader);
                            break;
                        default:
                            throw forge("Unknown description element: " + valueEvent.asStartElement().getName(),valueEvent.getLocation());
                    }
                }  else {
                    throw forge("Not implemented A : "+valueEvent,valueEvent.getLocation());
                }
            } else {
                throw forge("Not implemented B : "+valueEvent,valueEvent.getLocation());
            }
        }
        if (found.containsKey(Name.Q.XML_LANG)) {
            // description of a value
            Value value = found.remove(Name.Q.XML_LANG);
            found.forEach((key,val) -> value.addQualifier(new Name(key), ((SimpleValue)val).getContent()));
            return value;
        } else {
            // description of structure if all namespaces are equals
            Structure struct = new Structure();
            found.forEach((key, value) -> struct.add(new Name(key), value));
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
            XMLEvent valueEvent = peekNextNonIgnorable(reader);
            if (valueEvent.isCharacters()) {
                SimpleValue value = new SimpleValue(valueEvent.asCharacters().getData());
                // check if lang is present
                Attribute attr = ((StartElement) next).getAttributeByName(Name.Q.XML_LANG);
                if (attr != null) {
                    value.setXmlLang(attr.getValue());
                }
                array.addItem(value);
                reader.nextEvent(); // skipping the content
            } else if (valueEvent.isStartElement()) {
                if (Name.Q.RDF_DESCRIPTION.equals(valueEvent.asStartElement().getName())) {
                    // description in li
                    reader.nextEvent();
                    Value value = parseDescription(reader);
                    array.addItem(value);
                } else {
                    Value value = parseDescriptionContent(reader);
                    array.addItem(value);
                }
            } else {
                throw forge("Unexpected event in li: "+valueEvent,valueEvent.getLocation());
            }
            reader.nextTag();
            next = reader.nextTag();
        }
        peekNextNonIgnorable(reader);
        return array;
    }

    private Structure parseStructure (StartElement se, XMLEventReader reader) throws XMLStreamException {
        XMLEvent next = reader.nextTag();
        QName structName = se.getName();
        // opening rdf:Description
        // TODO ensure next is rdf:Description
        // parse fields
        next = reader.nextTag();
        // parse fields
        Structure struct = new Structure();
        next = parseStructureContent(struct,next,reader);
        // ending the structure description
        if (!next.isEndElement() || !Name.Q.RDF_DESCRIPTION.equals(next.asEndElement().getName())) {
            // expect closing structure
            throw forge("Expecting closing rdf:Description and found: "+next,next.getLocation());
        }
        next = reader.nextTag(); // closing rdf:Description
        if (!next.isEndElement() || !structName.equals(next.asEndElement().getName())) {
            // expect closing structure
            throw forge("Expecting closing structure '"+structName.toString()+"' and found: "+next,next.getLocation());
        }
        reader.nextTag(); // closing struct element
        return struct;
    }

    private XMLEvent parseStructureContent (Structure struct, XMLEvent next, XMLEventReader reader) throws XMLStreamException {
        while (next.isStartElement()) {
            QName fieldName = next.asStartElement().getName();
            // TODO limited : consider the value of a field is always simple
            String content = reader.nextEvent().asCharacters().getData();
            struct.add(new Name(fieldName),new SimpleValue(content));
            reader.nextTag(); // closing field element
            next = reader.nextTag();
        }
        return next;
    }


    private void loadNamespaces(StartElement se, Packet packet) {
        // load all namespaces
        Iterator namespaces = se.getNamespaces();
        while (namespaces.hasNext()) {
            Namespace namespace = (Namespace)namespaces.next();
            packet.getNamespaces().registerNamespace(namespace.getPrefix(), namespace.getNamespaceURI());
        }
    }

    private XMLEvent peekNextNonIgnorable(XMLEventReader reader) throws XMLStreamException {
        XMLEvent next = reader.peek();
        while (next.isCharacters() && next.asCharacters().isWhiteSpace()) {
            reader.nextEvent();
            next = reader.peek();
        }
        return next;
    }

    private XMLStreamException forge (String message, Location location) {
        return new XMLStreamException(String.format("%s at %d:%d(%d)",
                message,
                location.getLineNumber(),
                location.getColumnNumber(),
                location.getCharacterOffset()));
    }

}
