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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

/**
 * Created by Guillaume Bailleul on 29/08/2017.
 */
class ExtendedXMLEventReader implements XMLEventReader {

    private XMLEventReader reader;

    public ExtendedXMLEventReader(XMLEventReader reader) {
        this.reader = reader;
    }

    public XMLEvent peekNextNonIgnorable () throws XMLStreamException {
        XMLEvent next = reader.peek();
        while (next.isCharacters() && next.asCharacters().isWhiteSpace()) {
            reader.nextEvent();
            next = reader.peek();
        }
        return next;
    }

    public XMLEvent peekNextTag() throws XMLStreamException {
        XMLEvent event = peek();
        while (!(event instanceof StartElement) && !(event instanceof EndElement)) {
            if (event instanceof Characters) {
                String data = event.asCharacters().getData();
                if (((Characters) event).isWhiteSpace()) {
                  nextEvent();
                  event = peek();
                } else
                    throw new XMLStreamException("Next element is not a tag");
            } else if (event instanceof Comment) {
                nextEvent();
                event = peek();
            } else // found something different than tag
                throw new XMLStreamException("Next element is not a tag : "+event);
        }
        return event;
        /*
        while (next.isCharacters() && next.asCharacters().isWhiteSpace()) {
            reader.nextEvent();
            next = reader.peek();
        }
        return next;
        */
        /*
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

         */
    }


    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        return reader.nextEvent();
    }

    @Override
    public boolean hasNext() {
        return reader.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return reader.peek();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return reader.getElementText();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        return reader.nextTag();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return reader.getProperty(name);
    }

    @Override
    public void close() throws XMLStreamException {
        reader.close();
    }

    @Override
    public Object next() {
        return reader.next();
    }




}
