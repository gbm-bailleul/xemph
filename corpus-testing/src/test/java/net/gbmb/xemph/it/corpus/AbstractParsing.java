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

package net.gbmb.xemph.it.corpus;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.xml.XmpReader;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.util.Map;

public abstract class AbstractParsing {

    private File target;

    private PDDocument document;

    public AbstractParsing (File target) {
        this.target = target;
    }

    @Before
    public void init () throws Exception {
        document = PDDocument.load(target);
    }

    @After
    public void after () throws Exception {
        document.close();
    }

    @Test
    public void doTheJob () throws Exception {
        // load pdf file
        PDDocumentCatalog catalog = document.getDocumentCatalog();
        PDMetadata metadata = catalog.getMetadata();
        try {
            Packet packet = new XmpReader().parse(metadata.createInputStream());
            // check each property
            for (Map.Entry<Name, Value> entry: packet.getProperties().entrySet()) {
                if (!packet.getNamespaces().isDefined(entry.getKey().getNamespace(),entry.getKey().getLocalName()))
                    System.out.println(entry.getKey()+": "+entry.getValue());
            }
        } catch (XMLStreamException e) {
            // display xmp
            IOUtils.copyLarge(metadata.createInputStream(),System.err);
            throw e;
        }
    }

}
