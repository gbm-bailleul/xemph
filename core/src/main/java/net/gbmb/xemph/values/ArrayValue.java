package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public abstract class ArrayValue<T extends Value> extends Value {

    private List<T> items = new ArrayList<>();

    public void addItem(T item) {
        items.add(item);
    }

    public final List<T> getItems () {
        return Collections.unmodifiableList(items);
    }

    public final int size () {
        return items.size();
    }

    public String toString  () {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        items.forEach(i -> {
            sb.append(i);
            sb.append(", ");
        });
        sb.replace(sb.length()-2,sb.length()," ]");
        return sb.toString();
    }

}
