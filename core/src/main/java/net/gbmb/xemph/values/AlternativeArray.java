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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Alternative array wrapper
 */
public class AlternativeArray<T extends Value> extends ArrayValue<T> {

    private Map<String,T> langMap = new HashMap<>();

    public void addItem (T item) {
        String lang = item.getXmlLang();
        if (lang==null) lang = "x-default";
        langMap.put(lang,item);
        super.addItem(item);
    }

    public T getValue(String lang) {
        return langMap.get(lang);
    }

    public Set<String> getAlternativeKeySet() {
        return langMap.keySet();
    }

}
