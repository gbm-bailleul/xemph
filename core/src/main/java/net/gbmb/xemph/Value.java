package net.gbmb.xemph;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
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
