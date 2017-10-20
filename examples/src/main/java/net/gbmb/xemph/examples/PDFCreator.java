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
package net.gbmb.xemph.examples;

import net.gbmb.xemph.Packet;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.values.UnorderedArray;
import net.gbmb.xemph.xml.XmpWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.util.Date;

/**
 * Example of xmp extraction from a PDF, using PDFBox
 */
public class PDFCreator {

    public static void main (String [] args ) throws Exception {
        PDDocument document = new PDDocument();
        PDPage blankPage = new PDPage();
        document.addPage( blankPage );

        Packet packet = new Packet();
        packet.addProperty(DublinCore.CREATOR,"Guillaume Bailleul");
        packet.addProperty(DublinCore.DATE, new Date());
        packet.addProperty(DublinCore.PUBLISHER, UnorderedArray.parse("publisher1","publisher2","publisher3","publisher4"));

        XmpWriter writer = new XmpWriter();
        byte [] xmpContent = writer.write(packet);

        PDMetadata metadata = new PDMetadata(document);
        metadata.importXMPMetadata(xmpContent);
        document.getDocumentCatalog().setMetadata(metadata);

        document.save("target/example.pdf");

    }
}
