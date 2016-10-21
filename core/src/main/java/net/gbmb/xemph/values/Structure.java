package net.gbmb.xemph.values;

import net.gbmb.xemph.Name;
import net.gbmb.xemph.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class Structure extends Value {

    private Map<Name,Value> fields = new HashMap<>();

    public void add (Name name, Value value) {
        fields.put(name,value);
    }

    public Map<Name,Value> getFields () {
        return fields;
    }

}
