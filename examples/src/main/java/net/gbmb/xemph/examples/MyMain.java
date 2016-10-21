package net.gbmb.xemph.examples;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.UnorderedArray;
import net.gbmb.xemph.xml.RDFWriter;

import java.io.IOException;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class MyMain {

    // TODO remove this class

    public static void main (String [] args) throws IOException {
        Packet packet = new Packet();
        packet.addProperty(new Name(Namespaces.XMP,"BaseURL"),new SimpleValue("http://www.adobe.com/"));

//        Structure dim = new Structure();
//        dim.add(new Name(Namespaces.ST_DIM,"h"),new SimpleValue("11.0"));
//        dim.add(new Name(Namespaces.ST_DIM,"w"),new SimpleValue("8.5"));
//        dim.add(new Name(Namespaces.ST_DIM,"unit"),new SimpleValue("inch"));
//        packet.addProperty(new Name(Namespaces.XMP_TPG,"MaxPageSize"),dim);
//
//        UnorderedArray<SimpleValue> bag = new UnorderedArray<>();
//        bag.addItem(new SimpleValue("XMP"));
//        bag.addItem(new SimpleValue("metadata"));
//        bag.addItem(new SimpleValue("ISO Standard"));
//        packet.addProperty(new Name(Namespaces.DC,"subject"),bag);
//
//        OrderedArray<SimpleValue> seq = new OrderedArray<>();
//        seq.addItem(new SimpleValue("seq elem 1"));
//        seq.addItem(new SimpleValue("seq elem 2"));
//        seq.addItem(new SimpleValue("seq elem 3"));
//        seq.addItem(new SimpleValue("seq elem 4"));
//        packet.addProperty(new Name(Namespaces.DC,"title"),seq);

//        AlternativeArray<SimpleValue> alt = new AlternativeArray<>();
//        env1.setXmlLang("fr-fr");
//        alt.addItem(env1);
//        SimpleValue env2 = new SimpleValue("english title");
//        env2.setXmlLang("en-us");
//        alt.addItem(env2);
//        packet.addProperty(new Name(Namespaces.DC,"Example"),alt);

        packet.getNamespaces().registerNamespace("xe","http://ns.adobe.com/xmp-example/");
//        SimpleValue sv = new SimpleValue("Adobe XMP Specification");
//        sv.addQualifier(new Name("http://ns.adobe.com/xmp-example/","qualifier"),"artificial example");
//        packet.addProperty(new Name(Namespaces.DC,"source"),sv);

        UnorderedArray<SimpleValue> subject = new UnorderedArray<>();
        packet.addProperty(new Name(Namespaces.DC,"subject"),subject);
        subject.addItem(new SimpleValue("XMP"));
        SimpleValue v2 = new SimpleValue("Metadata");
        v2.addQualifier(new Name("http://ns.adobe.com/xmp-example/","qualifier"),"artificial example");
        subject.addItem(v2);

        RDFWriter writer = new RDFWriter();
        writer.setSimpleValueAsAttribute(false);
        writer.write(packet,System.out);
        System.out.flush();

    }
}
