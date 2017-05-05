package net.gbmb.xemph;

import net.gbmb.xemph.namespaces.Dimensions;
import net.gbmb.xemph.namespaces.DublinCore;
import net.gbmb.xemph.namespaces.Xmp;
import net.gbmb.xemph.namespaces.XmpTPg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class Namespaces {

    public static final String XML = "http://www.w3.org/XML/1998/namespace";

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private Map<String,String> namespaceToPrefix = new HashMap<>();

    private Map<String,String> prefixToNamespace = new HashMap<>();

    public Namespaces () {
        prefixToNamespace.put("xml",XML);
        addNamespace(new DublinCore());
        addNamespace(new Xmp());
        addNamespace(new XmpTPg());
        addNamespace(new Dimensions());
        // load revert map
        for (Map.Entry<String,String> entry:prefixToNamespace.entrySet()) {
            namespaceToPrefix.put(entry.getValue(),entry.getKey());
        }
    }

    private void addNamespace (Namespace ns) {
        prefixToNamespace.put(ns.getDefaultPrefix(),ns.getNamespaceURI());
    }

    public String getNamespaceFor (String prefix) {
        return prefixToNamespace.get(prefix);
    }

    public String getPrefixFor (String ns) {
        return namespaceToPrefix.get(ns);
    }

    public void registerNamespace(String prefix, String ns) {
        prefixToNamespace.put(prefix,ns);
        namespaceToPrefix.put(ns,prefix);
    }
}
