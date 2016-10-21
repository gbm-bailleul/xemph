package net.gbmb.xemph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class Namespaces {

    public static final String XML = "http://www.w3.org/XML/1998/namespace";

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String XMP = "http://ns.adobe.com/xap/1.0/";

    public static final String XMP_TPG = "http://ns.adobe.com/xap/1.0/t/pg/";

    public static final String ST_DIM = "http://ns.adobe.com/xap/1.0/sType/Dimensions#";

    public static final String DC = "http://purl.org/dc/elements/1.1/";

    private Map<String,String> namespaceToPrefix = new HashMap<>();

    private Map<String,String> prefixToNamespace = new HashMap<>();

    public Namespaces () {
        prefixToNamespace.put("xml",XML);
        prefixToNamespace.put("xmp",XMP);
        prefixToNamespace.put("xmpTPg",XMP_TPG);
        prefixToNamespace.put("stDim",ST_DIM);
        prefixToNamespace.put("dc",DC);
        // load revert map
        for (Map.Entry<String,String> entry:prefixToNamespace.entrySet()) {
            namespaceToPrefix.put(entry.getValue(),entry.getKey());
        }
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
