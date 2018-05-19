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

package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parent class for all array values
 */
public abstract class ArrayValue<T extends Value> extends Value {

    private List<T> items = new ArrayList<>();

    public void addItem(T item) {
        items.add(item);
    }

    public void addItems(T ... values)  {
        for (T value: values)
            addItem(value);
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
        sb.setCharAt(sb.length()-2,' ');
        sb.setCharAt(sb.length()-1,']');
        return sb.toString();
    }

    public final T getItem (int pos) {
        if (pos>=getItems().size()) {
            throw new ArrayIndexOutOfBoundsException(String.format("Array only contains %d elements, expecting pos %d",
                    getItems().size(),
                    pos)
            );
        }
        return items.get(pos);
    }

    public final SimpleValue getItemAsSimpleValue (int pos) {
        return SimpleValue.class.cast(getItem(pos));
    }

    public final Structure getItemAsStructure (int pos) {
        return Structure.class.cast(getItem(pos));
    }


}
