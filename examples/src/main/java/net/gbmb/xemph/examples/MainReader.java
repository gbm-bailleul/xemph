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
import net.gbmb.xemph.values.AlternativeArray;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.xml.XmlReader;

import java.io.InputStream;
import java.util.Map;

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
            System.out.println("     "+entry.getKey()+ " / "+entry.getValue().getClass());

        }

        System.out.println("> "+packet.contains(DublinCore.TITLE));
        System.out.println("> "+packet.getValue(DublinCore.TITLE));


        AlternativeArray<SimpleValue> aa = (AlternativeArray)packet.getValue(new Name(DublinCore.NAMESPACE_URI,"Example"));

        System.out.println("example fr: "+ aa.getValue("fr-fr"));


//        OrderedArray<SimpleValue> value = (OrderedArray<SimpleValue>)packet.getValue(dc.title());
//        for (SimpleValue sv : value.getItems()) {
//            System.out.println(">> "+sv);
//        }
//        System.out.println("aa "+((ParameterizedType)value.getItems().getGenericType()).getActualTypeArguments()[0]);

//        System.out.println(">>>>> "+value.getItem(2));

//        System.out.println("> "+packet.getValue(new Name(Namespaces.DC,"title"),OrderedArray.class));

    }
}
