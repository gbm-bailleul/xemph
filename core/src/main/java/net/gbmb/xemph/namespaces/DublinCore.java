package net.gbmb.xemph.namespaces;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Namespace;

/**
 * Created by Guillaume Bailleul on 07/03/2017.
 */
public class DublinCore extends Namespace {

    public static final String DEFAULT_PREFIX = "dc";

    public static final String NAMESPACE_URI = "http://purl.org/dc/elements/1.1/";

    public static final Name TITLE = new Name (NAMESPACE_URI,"title");

    @Override
    public String getDefaultPrefix() {
        return DEFAULT_PREFIX;
    }

    @Override
    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }


}
