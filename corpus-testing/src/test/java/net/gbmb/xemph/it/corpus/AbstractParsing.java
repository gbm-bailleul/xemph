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

/**
 * Created by Guillaume Bailleul on 27/06/2017.
 */
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
