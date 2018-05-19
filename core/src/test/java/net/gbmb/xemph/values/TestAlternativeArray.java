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

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestAlternativeArray {

    private AlternativeArray<SimpleValue> array = new AlternativeArray<>();

    public void init()  {
        array.addItem(new SimpleValue("Default title"));
        array.addItem(subInitValue("fr-fr","French title"));
        array.addItem(subInitValue("en-us","American title"));
    }

    private SimpleValue subInitValue (String lang, String value) {
        SimpleValue sv = new SimpleValue(value);
        sv.setXmlLang(lang);
        return sv;
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyItems () {
        init();
        array.getItems().add(new SimpleValue("value"));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void cannotRetrieveNonExistingItem () {
        init();
        array.getItem(3);
    }

    @Test
    public void ensureDefault () {
        array = new AlternativeArray<>();
        array.addItem(new SimpleValue("default title"));
        assertEquals(1, array.size());
        assertEquals(1, array.getAlternativeKeySet().size());
        assertEquals("x-default", array.getAlternativeKeySet().iterator().next());
        assertEquals("default title", array.getValue("x-default").asString());
    }

}
