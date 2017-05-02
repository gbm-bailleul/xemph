package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class OrderedArray<T extends Value> extends ArrayValue<T> {


    public T getItem (int pos) {
        if (pos>=getItems().size()) {
            throw new ArrayIndexOutOfBoundsException(String.format("Array only contains %d elements, expecting pos %d",
                    getItems().size(),
                    pos)
            );
        }
        return getItems().get(pos);
    }


}
