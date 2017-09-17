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
import net.gbmb.xemph.namespaces.Dimensions;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.namespaces.Xmp;
import net.gbmb.xemph.namespaces.XmpTPg;
import net.gbmb.xemph.values.*;
import net.gbmb.xemph.xml.XmlWriter;

import java.io.IOException;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class MyMain {

    public static void main (String [] args) throws IOException {
        Packet packet = new Packet();
        packet.addProperty(new Name(Xmp.NAMESPACE_URI,"BaseURL"),new SimpleValue("http://www.adobe.com/"));

        Structure dim = new Structure();
        dim.add(Dimensions.H,new SimpleValue("11.0"));
        dim.add(Dimensions.W,new SimpleValue("8.5"));
        dim.add(Dimensions.UNIT,new SimpleValue("inch"));
        packet.addProperty(XmpTPg.MAX_PAGE_SIZE,dim);

        UnorderedArray<SimpleValue> bag = new UnorderedArray<>();
        bag.addItem(new SimpleValue("XMP"));
        bag.addItem(new SimpleValue("metadata"));
        bag.addItem(new SimpleValue("ISO Standard"));
        packet.addProperty(DublinCore.SUBJECT,bag);

        OrderedArray<SimpleValue> seq = new OrderedArray<>();
        seq.addItem(new SimpleValue("seq elem 1"));
        seq.addItem(new SimpleValue("seq elem 2"));
        seq.addItem(new SimpleValue("seq elem 3"));
        seq.addItem(new SimpleValue("seq elem 4"));
        packet.addProperty(DublinCore.TITLE,seq);

        AlternativeArray<SimpleValue> alt = new AlternativeArray<>();
        SimpleValue env1 = new SimpleValue("titre français");
        env1.setXmlLang("fr-fr");
        alt.addItem(env1);
        SimpleValue env2 = new SimpleValue("english title");
        env2.setXmlLang("en-us");
        alt.addItem(env2);
        packet.addProperty(new Name(DublinCore.NAMESPACE_URI,"Example"),alt);

        packet.getNamespaces().registerNamespace("xe","http://ns.adobe.com/xmp-example/");
        SimpleValue sv = new SimpleValue("Adobe XMP Specification");
        sv.addQualifier(new Name("http://ns.adobe.com/xmp-example/","qualifier"),"artificial example");
        packet.addProperty(new Name(DublinCore.NAMESPACE_URI,"source"),sv);

        UnorderedArray<SimpleValue> subject = new UnorderedArray<>();
        packet.addProperty(new Name(DublinCore.NAMESPACE_URI,"subject2"),subject);
        subject.addItem(new SimpleValue("XMP"));
        SimpleValue v2 = new SimpleValue("Metadata");
        v2.addQualifier(new Name("http://ns.adobe.com/xmp-example/","qualifier"),"artificial example");
        subject.addItem(v2);

        XmlWriter writer = new XmlWriter();
        writer.setSimpleValueAsAttribute(false);
        writer.write(packet,System.out);
        System.out.flush();

    }
}
