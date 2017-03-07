package net.gbmb.xemph.examples;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespaces;
import net.gbmb.xemph.Packet;
import net.gbmb.xemph.Value;
import net.gbmb.xemph.values.OrderedArray;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.UnorderedArray;
import net.gbmb.xemph.xml.XmlReader;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 21/10/2016.
 */
public class MainReader {


    public static void main (String [] args) throws Exception {
        InputStream is = MainReader.class.getResourceAsStream("/input.xml");
        XmlReader reader = new XmlReader();
        Packet packet = reader.parse(is);

        System.err.flush();
        System.out.flush();

        System.out.println("Namespaces");
        for (String ns:packet.listUsedNamespaces()) {
            System.out.println("      "+ns);
        }
        System.out.println("Properties");
        for (Map.Entry<Name,Value> entry: packet.getProperties().entrySet()) {
            System.out.println("     "+entry.getKey());

        }

        OrderedArray<SimpleValue> value = (OrderedArray<SimpleValue>)packet.getProperty(Namespaces.DC,"title");
        for (SimpleValue sv : value.getItems()) {
            System.out.println(">> "+sv);
        }
//        System.out.println("aa "+((ParameterizedType)value.getItems().getGenericType()).getActualTypeArguments()[0]);

        System.out.println(">>>>> "+value.getItem(2));

//        System.out.println("> "+packet.getProperty(new Name(Namespaces.DC,"title"),OrderedArray.class));

    }
}
