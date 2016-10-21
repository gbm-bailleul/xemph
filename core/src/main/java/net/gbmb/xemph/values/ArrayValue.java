package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public abstract class ArrayValue<T extends Value> extends Value {

    private List<T> items = new ArrayList<>();

    public final void addItem(T item) {
        items.add(item);
    }

    public final List<T> getItems () {
        return items;
    }

}
