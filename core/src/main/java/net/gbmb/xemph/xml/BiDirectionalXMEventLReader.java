package net.gbmb.xemph.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Guillaume Bailleul on 29/08/2017.
 */
class BiDirectionalXMEventLReader implements XMLEventReader {

    private XMLEventReader reader;

    private List<XMLEvent> re = new ArrayList<>();

    private int p = 0;

    private boolean record = false;

    private Pattern whiteSpacePattern = Pattern.compile("^\\s+$");

    public BiDirectionalXMEventLReader(XMLEventReader reader) {
        this.reader = reader;
    }

    public void startRecord() {
        record = true;
    }

    public void endRecord() {
        record = false;
        // remove all records before position p
        for (int i=0; i < p ; i++) {
            re.remove(0);
        }
        p=0;
    }

    public void back () throws XMLStreamException {
        if (p>0) {
            p--;
        } else {
            throw new XMLStreamException("Cannot go back in stream");
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        if (p < re.size()) {
            return re.get(p++);
        }
        XMLEvent next =  reader.nextEvent();
        if (record) {
            re.add(next);
            p = re.size();
        }
        return next;
    }

    @Override
    public boolean hasNext() {
        if (p < re.size()) {
            return true;
        }
        return reader.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (p < re.size()) {
            return re.get(p);
        }
        return reader.peek();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        // not used so not implemented
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        XMLEvent event = nextEvent();
        while (!(event instanceof StartElement) && !(event instanceof EndElement)) {
            if (event instanceof Characters) {
                String data = event.asCharacters().getData();
                if (data == null || data.length() == 0 || whiteSpacePattern.matcher(data).matches())
                    event = nextEvent();
                else
                    throw new XMLStreamException("Next element is not a tag");
            } else if (event instanceof Comment) {
                event = nextEvent();
            } else // found something different than tag
                throw new XMLStreamException("Next element is not a tag : "+event);
        }
        return event;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        // not used so not implemented
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public void close() throws XMLStreamException {
        reader.close();
    }

    @Override
    public Object next() {
        // not used so not implemented
        throw new IllegalArgumentException("Not implemented");
    }

}
