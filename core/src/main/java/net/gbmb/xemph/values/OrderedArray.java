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

/**
 * Set wrapper
 */
public class OrderedArray<T extends Value> extends ArrayValue<T> {

    /**
     * Create an {@link OrderedArray} of {@link SimpleValue}.
     * Each parameter will be an element of the {@link OrderedArray}
     * @param values
     * @return
     */
    public static OrderedArray<SimpleValue> parse (Object ... values) {
        OrderedArray<SimpleValue> ret = new OrderedArray<>();
        for (Object value: values) {
            ret.addItem(SimpleValue.parse(value));
        }
        return ret;
    }

}
