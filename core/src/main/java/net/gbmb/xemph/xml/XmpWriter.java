package net.gbmb.xemph.xml;

import net.gbmb.xemph.Packet;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class XmpWriter {


    public static final String XPACKET_ID = "W5M0MpCehiHzreSzNTczkc9d";

    public static final String XPACKET_BEGIN = "\uFEFF";

    private XmlWriter xmlWriter;

    public XmpWriter(XmlWriter writer) {
        this.xmlWriter = writer;
    }

    public XmpWriter() {
        this(new XmlWriter());
    }

    public void write (Packet packet, OutputStream output) throws IOException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty("javax.xml.stream.isRepairingNamespaces",true);
            XMLStreamWriter writer = factory.createXMLStreamWriter(output);
            // start processing instruction
            writer.writeProcessingInstruction("xpacket","begin=\""+XPACKET_BEGIN+"\" id=\""+XPACKET_ID+"\"");
            // xmp meta element
            writer.writeStartElement("x","xmpmeta","adobe:ns:meta/");
            // packet content
            xmlWriter.writePacket(packet,writer);
            // end xmp meta
            writer.writeEndElement();
            // ending processing instruction
            writer.writeProcessingInstruction("xpacket","end=\"w\"");
        } catch (XMLStreamException e) {
            throw new IOException("Failed to serialize packet",e);
        }

    }

    public byte [] write (Packet packet) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        write(packet,bos);
        bos.close();
        return bos.toByteArray();
    }

}
