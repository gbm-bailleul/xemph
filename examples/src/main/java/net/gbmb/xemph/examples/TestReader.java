package net.gbmb.xemph.examples;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.values.AlternativeArray;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.xml.XmlReader;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 21/10/2016.
 */
public class TestReader {


    public static void main (String [] args) throws Exception {
        InputStream is = TestReader.class.getResourceAsStream("/test.xml");
        XmlReader reader = new XmlReader();
        Packet packet = reader.parse(is);

//        System.err.flush();
//        System.out.flush();
//
//        System.out.println("Namespaces");
//        for (String ns:packet.listUsedNamespaces()) {
//            System.out.println("      "+ns);
//        }
//        System.out.println("Properties");
//        for (Map.Entry<Name,Value> entry: packet.getProperties().entrySet()) {
//            System.out.println("     "+entry.getKey()+ " / "+entry.getValue().getClass());
//
//        }
//
//        System.out.println("> "+packet.contains(DublinCore.TITLE));
//        System.out.println("> "+packet.getValue(DublinCore.TITLE));
//
//
//        AlternativeArray<SimpleValue> aa = packet.getValue(new Name(DublinCore.NAMESPACE_URI,"Example"),AlternativeArray.class);
//
//        System.out.println("example fr: "+ aa.getValue("fr-fr"));


//        OrderedArray<SimpleValue> value = (OrderedArray<SimpleValue>)packet.getValue(dc.title());
//        for (SimpleValue sv : value.getItems()) {
//            System.out.println(">> "+sv);
//        }
//        System.out.println("aa "+((ParameterizedType)value.getItems().getGenericType()).getActualTypeArguments()[0]);

//        System.out.println(">>>>> "+value.getItem(2));

//        System.out.println("> "+packet.getValue(new Name(Namespaces.DC,"title"),OrderedArray.class));

    }
}
