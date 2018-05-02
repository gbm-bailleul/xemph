package net.gbmb.xemph.xml;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.OrderedArray;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.Structure;
import org.junit.Test;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class TestXmlWriter {

    public static final String TARGET = "my target";

    public static final String EXAMPLE_NS = "http://www.example.com";


    public Packet goAndBack (Packet packet) throws Exception {
        XmlWriter writer = new XmlWriter();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        writer.write(packet,output);
        output.close();
        XmlReader reader = new XmlReader();
        return reader.parse(new ByteArrayInputStream(output.toByteArray()));
    }


    @Test
    public void writeEmptyDefinition () throws Exception {
        Packet packet = new Packet();
        packet.setTargetResource(TARGET);
        Packet result = goAndBack(packet);
        assertEquals(0,result.getProperties().size());
    }

    @Test
    public void writeSimpleValue () throws Exception {
        Packet packet = new Packet();
        packet.setTargetResource(TARGET);
        // Simple Property
        packet.addProperty(
                new Name(EXAMPLE_NS,"prop"),
                "SimpleString"
        );
        Packet result = goAndBack(packet);
        assertEquals(1,result.getProperties().size());
        // Check Property
        Value simple = result.getValue(new Name (EXAMPLE_NS,"prop"));
        assertTrue(simple instanceof SimpleValue);
        assertEquals("SimpleString",((SimpleValue)simple).asString());
    }

    @Test
    public void writeSimpleValueWithQualifier () throws Exception {
        Packet packet = new Packet();
        packet.setTargetResource(TARGET);
        // Simple Property
        SimpleValue simple = new SimpleValue("SimpleString");
        simple.addQualifier(new Name(EXAMPLE_NS,"q1"),"qual1");
        simple.addQualifier(new Name(EXAMPLE_NS,"q2"),"qual2");
        packet.addProperty(
                new Name(EXAMPLE_NS,"prop"),
                simple
        );
        Packet result = goAndBack(packet);
        assertEquals(1,result.getProperties().size());
        // Check Property
        Value s = result.getValue(new Name (EXAMPLE_NS,"prop"));
        assertTrue(s instanceof SimpleValue);
        assertEquals("SimpleString",((SimpleValue)s).asString());
        assertEquals(2,s.getQualifiers().size());
        assertEquals("qual1",s.getQualifier(new Name(EXAMPLE_NS,"q1")));
    }

    @Test
    public void writeStructure () throws Exception {
        Packet packet = new Packet();
        packet.setTargetResource(TARGET);
        // Simple Property
        Structure s = new Structure();
        s.add(new Name(EXAMPLE_NS,"item1"),SimpleValue.parse("value1"));
        s.add(new Name(EXAMPLE_NS,"item2"),SimpleValue.parse("value2"));
        s.add(new Name(EXAMPLE_NS,"item3"),SimpleValue.parse("value3"));
        packet.addProperty(
                new Name(EXAMPLE_NS,"prop"),
                s
        );
        Packet result = goAndBack(packet);
        assertEquals(1,result.getProperties().size());
        // Check values
        Value value = result.getValue(new Name (EXAMPLE_NS,"prop"));
        assertTrue(value instanceof Structure);
        Structure struct = (Structure)value;
        assertEquals(3,struct.size());
        assertEquals(0, struct.getQualifiers().size());
        SimpleValue simple = (SimpleValue) struct.getField(new Name(EXAMPLE_NS,"item1"));
        assertEquals("value1",simple.asString());
        assertEquals(0, simple.getQualifiers().size());
    }

    @Test
    public void writOrderedArray () throws Exception {
        Packet packet = new Packet();
        packet.setTargetResource(TARGET);
        // Simple Property
        OrderedArray<SimpleValue> s = OrderedArray.parse(new String [] {"value1","value2"});
        packet.addProperty(
                new Name(EXAMPLE_NS,"prop"),
                s
        );
        Packet result = goAndBack(packet);
        assertEquals(1,result.getProperties().size());
        // Check values
        Value value = result.getValue(new Name (EXAMPLE_NS,"prop"));
        assertTrue(value instanceof OrderedArray);
        OrderedArray struct = (OrderedArray)value;
        assertEquals(2,struct.size());
        assertEquals(0, struct.getQualifiers().size());
        SimpleValue simple = (SimpleValue) struct.getItem(0);
        assertEquals("value1",simple.asString());
        assertEquals(0, simple.getQualifiers().size());
    }


}
