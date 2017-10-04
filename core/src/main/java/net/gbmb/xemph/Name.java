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

package net.gbmb.xemph;

import javax.xml.namespace.QName;

/**
 * Convenience class to handle naming of elements
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Name that = (Name) o;

        //noinspection SimplifiableIfStatement
        if (!namespace.equals(that.namespace))
            return false;
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

        public static final QName RDF_RDF = new QName(Namespaces.RDF,"RDF");

        public static final QName RDF_VALUE = new QName(Namespaces.RDF,"value");

        public static final QName RDF_DESCRIPTION = new QName(Namespaces.RDF,"Description");

        public static final QName XMP_META = new QName(Namespaces.ADOBE_META,"xmpmeta");

        private Q () {}
    }

}
