package net.gbmb.xemph;

import javax.xml.namespace.QName;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class Name {

    private String namespace; // URI

    private String localName;

    public Name(String namespace, String localName) {
        this.namespace = namespace;
        this.localName = localName;
    }

    public Name(QName qn) {
        this(qn.getNamespaceURI(),qn.getLocalPart());
    }


    public String getNamespace() {
        return namespace;
    }

    public String getLocalName() {
        return localName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name that = (Name) o;

        //noinspection SimplifiableIfStatement
        if (!namespace.equals(that.namespace)) return false;
        return localName.equals(that.localName);

    }

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + localName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "'"+namespace+"':'"+localName+"'";
    }


    public static class Q {

        public static final QName XML_LANG = new QName(Namespaces.XML,"lang");
    }

}
