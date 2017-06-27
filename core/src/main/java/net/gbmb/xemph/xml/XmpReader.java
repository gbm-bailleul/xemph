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
 * Created by Guillaume Bailleul on 27/06/2017.
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
