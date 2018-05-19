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

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.namespaces.Xmp;
import net.gbmb.xemph.namespaces.XmpMM;
import net.gbmb.xemph.values.AlternativeArray;
import net.gbmb.xemph.values.OrderedArray;
import net.gbmb.xemph.xml.XmpReader;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Example of xmp extraction from a PDF, using PDFBox
 */
public class PDFExtractor {

    public static void main (String [] args ) throws Exception {
        File input = new File ("files/XMPSpecificationPart1.pdf");
        RandomAccessFile raf = new RandomAccessFile(input,"r");
        PDFParser parser = new PDFParser(raf);
        parser.parse();
        PDDocument document = parser.getPDDocument();
        InputStream inputstream = document.getDocumentCatalog().getMetadata().exportXMPMetadata();
//        IOUtils.copy(inputstream,System.out);
        XmpReader xmpReader = new XmpReader();
        Packet packet = xmpReader.parse(inputstream);
        for (Map.Entry<Name,Value> entry: packet.getProperties().entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue().getClass()+" / "+entry.getValue());
        }
        System.out.println(packet.getSimpleValue(Xmp.METADATA_DATE));
        System.out.println(packet.getSimpleValue(Xmp.METADATA_DATE).asDate().getTime());
        System.out.println();
        System.out.println(packet.getSimpleValue(XmpMM.INSTANCE_ID));
        System.out.println(packet.getSimpleValue(XmpMM.INSTANCE_ID).asUUID());

        OrderedArray<Value> creators = DublinCore.getCreator(packet);
        for (Value sv : creators.getItems()) {
            System.out.println(">> "+sv.getClass().getSimpleName()+" : "+sv.toString());
        }


        System.out.println("Contains DC:title = "+DublinCore.containsTitle(packet));
        System.out.println("Contains DC:title = "+ packet.contains(DublinCore.TITLE));
        AlternativeArray<Value> title = DublinCore.getTitle(packet);

        for (String key : title.getAlternativeKeySet()) {
            System.out.println(">> "+key+" : "+title.getValue(key));
        }
    }
}
