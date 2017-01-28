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
        if (!replace) {
            // cannot add property if name is already used
            if (properties.containsKey(name)) {
                throw new IllegalArgumentException("Cannot have two property with same name: "+name);
            }
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

    public Collection<Name> getPropertiesNames () {
        return properties.keySet();
    }

    public Value getProperty (Name name) {
        return properties.get(name);
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public Collection<String> listUsedNamespaces() {
        List<String> result = new ArrayList<>();
        // add properties namespaces
        for (Name name:properties.keySet()) {
            String ns = name.getNamespace();
            if (!result.contains(ns)) {
                result.add(ns);
            }
            Value value = properties.get(name);
            if (value instanceof Structure) {
                // add namespaces of structure fields
                for (Name name1: ((Structure)value).getFields().keySet()) {
                    String ns1 = name1.getNamespace();
                    if (!result.contains(ns1)) {
                        result.add(ns1);
                    }
                }
            } else if (value instanceof ArrayValue) {
                // add namespaces of qualifiers of each value
                for (Value value1:((ArrayValue<? extends Value>)value).getItems()) {
                    for (Name name1: value1.getQualifiers().keySet()) {
                        String ns1 = name1.getNamespace();
                        if (!result.contains(ns1)) {
                            result.add(ns1);
                        }
                    }
                }
            }
            // add namespaces of value qualifiers
            for (Name name1: value.getQualifiers().keySet()) {
                String ns1 = name1.getNamespace();
                if (!result.contains(ns1)) {
                    result.add(ns1);
                }
            }
        }
        return result;
    }



}
