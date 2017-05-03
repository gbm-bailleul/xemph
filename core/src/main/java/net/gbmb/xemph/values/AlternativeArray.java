package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class AlternativeArray<T extends Value> extends ArrayValue<T> {

    private Map<String,T> langMap = new HashMap<String, T>();

    public void addItem (T item) {
        langMap.put(item.getXmlLang(),item);
        super.addItem(item);
    }

    public T getValue(String lang) {
        return langMap.get(lang);
    }

}
