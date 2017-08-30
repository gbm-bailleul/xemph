package net.gbmb.xemph.xml;

import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;


/**
 * Created by Guillaume Bailleul on 30/08/2017.
 */
public class TestBiDirectionalXMLEventReader {


    @Test
    public void simpleChainNoRecord () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        events.add(prepareMockCharacters("C"));

        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));

        assertEquals("A",((Characters)reader.peek()).getData());
        assertEquals("A",((Characters)reader.nextEvent()).getData());
        assertEquals("B",((Characters)reader.peek()).getData());
        assertEquals("B",((Characters)reader.nextEvent()).getData());
        assertTrue(reader.hasNext());
        assertEquals("C",((Characters)reader.nextEvent()).getData());
        assertFalse(reader.hasNext());
        reader.close();
    }


    @Test
    public void simpleChainWithRecord () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        events.add(prepareMockCharacters("C"));

        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.startRecord();
        assertEquals("A",((Characters)reader.nextEvent()).getData());
        assertEquals("B",((Characters)reader.nextEvent()).getData());
        assertTrue(reader.hasNext());
        // go back
        reader.back();
        assertTrue(reader.hasNext());
        assertEquals("B",((Characters)reader.peek()).getData());
        assertEquals("B",((Characters)reader.nextEvent()).getData());
        // go back twice
        reader.back();
        reader.back();
        assertEquals("A",((Characters)reader.peek()).getData());
        assertEquals("A",((Characters)reader.nextEvent()).getData());
        assertEquals("B",((Characters)reader.peek()).getData());
        assertEquals("B",((Characters)reader.nextEvent()).getData());
        assertEquals("C",((Characters)reader.nextEvent()).getData());
        assertFalse(reader.hasNext());
    }

    @Test
    public void simpleChainWithRecordAndEnd () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        events.add(prepareMockCharacters("C"));
        events.add(prepareMockCharacters("D"));
        events.add(prepareMockCharacters("E"));

        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.startRecord();
        assertEquals("A",((Characters)reader.nextEvent()).getData());
        assertEquals("B",((Characters)reader.nextEvent()).getData());
        assertEquals("C",((Characters)reader.nextEvent()).getData());
        reader.endRecord();
        // go back
        assertEquals("D",((Characters)reader.nextEvent()).getData());
        assertEquals("E",((Characters)reader.nextEvent()).getData());
    }


    @Test(expected = XMLStreamException.class)
    public void simpleChainWithBackAfterEnd () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.startRecord();
        assertEquals("A",((Characters)reader.nextEvent()).getData());
        reader.endRecord();
        // go back fails
        reader.back();
    }

    @Test
    public void testNextTagNoRecord () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockStartElement("A"));
        events.add(prepareMockCharacters(""));
        events.add(prepareMockCharacters(null));
        events.add(prepareMockStartElement("B"));
        events.add(prepareMockEndElement("B"));
        events.add(prepareMockEndElement("A"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        assertEquals(events.get(0).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        assertEquals(events.get(3).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        assertEquals(events.get(3).asStartElement().getName(),reader.nextTag().asEndElement().getName());
        assertEquals(events.get(0).asStartElement().getName(),reader.nextTag().asEndElement().getName());
    }

    @Test
    public void testNextTagWithRecord () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockStartElement("A"));
        events.add(prepareMockCharacters(""));
        events.add(prepareMockCharacters(null));
        events.add(prepareMockStartElement("B"));
        events.add(prepareMockEndElement("B"));
        events.add(prepareMockEndElement("A"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.startRecord();

        assertEquals(events.get(0).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        reader.back();
        assertEquals(events.get(0).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        assertEquals(events.get(3).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        reader.back();
        reader.back();
        assertEquals(events.get(3).asStartElement().getName(),reader.nextTag().asStartElement().getName());
        assertEquals(events.get(3).asStartElement().getName(),reader.nextTag().asEndElement().getName());
        assertEquals(events.get(0).asStartElement().getName(),reader.nextTag().asEndElement().getName());
    }



    @Test(expected = IllegalArgumentException.class)
    public void ensureNotImplementedGetElementText () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.getElementText();
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNotImplementedGetProperty () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.getProperty("A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureNotImplementedNext () throws Exception {
        List<XMLEvent> events = new ArrayList<>();
        events.add(prepareMockCharacters("A"));
        events.add(prepareMockCharacters("B"));
        BiDirectionalXMEventLReader reader = new BiDirectionalXMEventLReader(new TestXMLEventReader(events));
        reader.next();
    }

    private Characters prepareMockCharacters(String value) {
        Characters ca = Mockito.mock(Characters.class);
        Mockito.doReturn(value).when(ca).getData();
        Mockito.doReturn(ca).when(ca).asCharacters();
        return ca;
    }

    private StartElement prepareMockStartElement (String name) {
        StartElement se = Mockito.mock(StartElement.class);
        Mockito.doReturn(new QName("A",name, "A")).when(se).getName();
        Mockito.doReturn(se).when(se).asStartElement();
        return se;
    }

    private EndElement prepareMockEndElement (String name) {
        EndElement se = Mockito.mock(EndElement.class);
        Mockito.doReturn(new QName("A",name, "A")).when(se).getName();
        Mockito.doReturn(se).when(se).asEndElement();
        return se;
    }

    private static class TestXMLEventReader implements XMLEventReader {

        private final List<XMLEvent> events;
        int pos;

        public TestXMLEventReader(List<XMLEvent> events) {
            this.events = events;
            pos = 0;
        }

        @Override
        public XMLEvent nextEvent() throws XMLStreamException {
            try {
                return events.get(pos++);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException("No more element in mock");
            }
        }

        @Override
        public boolean hasNext() {
            return pos<events.size();
        }

        @Override
        public XMLEvent peek() throws XMLStreamException {
            try {
                return events.get(pos);
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException("No more element in mock");
            }
        }

        @Override
        public String getElementText() throws XMLStreamException {
            return null;
        }

        @Override
        public XMLEvent nextTag() throws XMLStreamException {
            return null;
        }

        @Override
        public Object getProperty(String name) throws IllegalArgumentException {
            return null;
        }

        @Override
        public void close() throws XMLStreamException {

        }

        @Override
        public Object next() {
            return null;
        }
    }
}
