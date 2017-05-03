package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class UnorderedArray<T extends Value> extends ArrayValue<T> {

    @Override
    public void addItem(T item) {
        // ensure item is not already in array (swallow if already existing)
        if (getItems().contains(item))
            return;
        super.addItem(item);
    }
}
