package net.gbmb.xemph;

import net.gbmb.xemph.namespaces.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class Namespaces {

    public static final String XML = "http://www.w3.org/XML/1998/namespace";

    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String ADOBE_META = "adobe:ns:meta/";

    private Map<String,String> namespaceToPrefix = new HashMap<>();

    private Map<String,String> prefixToNamespace = new HashMap<>();

    private Map<String,Namespace> namespaceByURL = new HashMap<>();

    public Namespaces () {
        prefixToNamespace.put("xml",XML);
        addNamespace(new DublinCore());
        addNamespace(new Xmp());
        addNamespace(new XmpTPg());
        addNamespace(new Dimensions());
        addNamespace(new PdfNamespace());
        addNamespace(new PdfaId());
        addNamespace(new XmpMM());
    }

    private void addNamespace (Namespace ns) {
        prefixToNamespace.put(ns.getDefaultPrefix(),ns.getNamespaceURI());
        namespaceToPrefix.put(ns.getNamespaceURI(),ns.getDefaultPrefix());
        namespaceByURL.put(ns.getNamespaceURI(),ns);
    }

    public Namespace getNamespaceByDefaultPrefix (String prefix) {
        String ns =  prefixToNamespace.get(prefix);
        return namespaceByURL.get(ns);
    }

    public Namespace getNamespaceByURL (String url) {
        return namespaceByURL.get(url);
    }

    public String getPrefixFor (String ns) {
        return namespaceToPrefix.get(ns);
    }

    public void registerNamespace(String prefix, String ns) {
        prefixToNamespace.put(prefix,ns);
        namespaceToPrefix.put(ns,prefix);
    }

    public boolean isDefined (Name name) {
        return isDefined(name.getNamespace(),name.getLocalName());
    }

    public boolean isDefined (String ns, String name) {
        Namespace namespace = namespaceByURL.get(ns);
        if (namespace==null)
            return false;
        // TODO should create namespace.isDefined(name)
        return namespace.getPropertyType(name)!=null;
    }

    public Class getType (Name name) {
        Namespace namespace = namespaceByURL.get(name.getNamespace());
        if (namespace==null)
            return null; // TODO throw exception ?
        return namespace.getPropertyType(name.getLocalName());
    }
}
