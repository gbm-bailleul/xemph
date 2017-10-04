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

import java.util.HashMap;
import java.util.Map;

/**
 * The parent class for all values of a RDF property.
 */
public abstract class Value {

    private String xmlLang;

    private Map<Name,String> qualifiers = new HashMap<>();

    public void addQualifier (Name name, String qualifier) {
        qualifiers.put(name,qualifier);
    }

    public Value qualifier(Name name, String qualifier) {
        addQualifier(name,qualifier);
        return this;
    }

    public boolean hasQualifiers() {
        return qualifiers.size()>0;
    }

    public Map<Name,String> getQualifiers () {
        return qualifiers;
    }

    public String getQualifier(Name name) {
        return qualifiers.get(name);
    }

    public boolean containsQualifier(Name name) {
        return qualifiers.containsKey(name);
    }


    public String getXmlLang() {
        return xmlLang;
    }

    public void setXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
    }
}
