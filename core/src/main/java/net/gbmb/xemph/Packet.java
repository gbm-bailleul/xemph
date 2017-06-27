package net.gbmb.xemph;

import net.gbmb.xemph.values.ArrayValue;
import net.gbmb.xemph.values.SimpleValue;
import net.gbmb.xemph.values.Structure;

import java.util.*;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
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

    public void addProperty (Name name, String value) {
        addProperty(name,new SimpleValue(value));
    }

    public void addProperty (Name name, String value, boolean replace) {
        addProperty(name,new SimpleValue(value),replace);
    }

    public Collection<Name> getPropertiesNames () {
        return properties.keySet();
    }

    public Value getValue(Name name) {
        return properties.get(name);
    }

    public Value getValue(String namespace, String localName) {
        return getValue(new Name(namespace,localName));
    }

    public <R extends Value> R getValue(Name name, Class<R> type) {
        return type.cast(getValue(name));
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
                for (Name name1: ((Structure)entry.getValue()).getFields().keySet()) {
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
