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

import net.gbmb.xemph.values.ArrayValue;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.Structure;

import java.util.*;

/**
 * The object form of an xmp description
 */
public class Packet {

    private Map<Name,Value> properties = new HashMap<>();

    private String targetResource;

    private Namespaces namespaces = new Namespaces();

    public Map<Name, Value> getProperties() {
        return properties;
    }

    public String getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
    }

    public void addProperty (Name name, Value value, boolean replace) {
        // check name is not null
        if (name==null) {
            throw new IllegalArgumentException("Property name cannot be null");
        }
        // check value is not null
        if (value==null) {
            throw new IllegalArgumentException("Property value cannot be null");
        }
        if (!replace && properties.containsKey(name)) {
            // cannot add property if name is already used
            throw new IllegalArgumentException("Cannot have two property with same name: "+name);
        }
        // add or replace
        properties.put(name,value);
    }

    public void addProperty (Name name, Value value) {
        // add without replacing if existing
        addProperty(name,value,false);
    }

    public void addProperty (Name name, Object value) {
        addProperty(name,SimpleValue.parse(value));
    }

    public void addProperty (Name name, Object value, boolean replace) {
        addProperty(name,SimpleValue.parse(value),replace);
    }

    public Collection<Name> getPropertiesNames () {
        return properties.keySet();
    }

    /**
     * Return the Value associated with the Name
     * @param name identifying the value
     * @return Value or null if not existing
     */
    public Value getValue(Name name) {
        return properties.get(name);
    }

    public SimpleValue getSimpleValue(Name name) throws InvalidTypeConvertException {
        try {
            return SimpleValue.class.cast(properties.get(name));
        } catch (ClassCastException e) {
            throw new InvalidTypeConvertException("Cannot convert to simple type",e);
        }
    }

    public boolean contains (Name name) {
        return properties.containsKey(name);
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public Collection<String> listUsedNamespaces() {
        List<String> result = new ArrayList<>();
        // add properties namespaces
        for (Map.Entry<Name,Value> entry:properties.entrySet()) {
            String ns = entry.getKey().getNamespace();
            if (!result.contains(ns)) {
                result.add(ns);
            }
            if (entry.getValue() instanceof Structure) {
                // add namespaces of structure fields
                for (Name name1: ((Structure)entry.getValue()).keySet()) {
                    String ns1 = name1.getNamespace();
                    if (!result.contains(ns1)) {
                        result.add(ns1);
                    }
                }
            } else if (entry.getValue() instanceof ArrayValue) {
                // add namespaces of qualifiers of each value
                for (Value value1:((ArrayValue<? extends Value>)entry.getValue()).getItems()) {
                    for (Name name1: value1.getQualifiers().keySet()) {
                        String ns1 = name1.getNamespace();
                        if (!result.contains(ns1)) {
                            result.add(ns1);
                        }
                    }
                }
            }
            // add namespaces of value qualifiers
            for (Name name1: entry.getValue().getQualifiers().keySet()) {
                String ns1 = name1.getNamespace();
                if (!result.contains(ns1)) {
                    result.add(ns1);
                }
            }
        }
        return result;
    }



}
