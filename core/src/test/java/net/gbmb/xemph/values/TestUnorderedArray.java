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

import static org.junit.Assert.*;

public class TestUnorderedArray {

    private UnorderedArray<SimpleValue> array = new UnorderedArray<>();

    public TestUnorderedArray()  {
        array.addItem(new SimpleValue("value1"));
        array.addItem(new SimpleValue("value2"));
        array.addItem(new SimpleValue("value3"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyItems () {
        array.getItems().add(new SimpleValue("value"));
    }

    @Test
    public void cannotHaveSameElementMultipleTime () {
        array.addItem(array.getItems().get(0));
        assertEquals(3,array.getItems().size());
    }

    @Test
    public void testPossibleParse () throws Exception {
        String [] initials = new String [] { "value1", "value2", "value3", "value1"};
        UnorderedArray<SimpleValue> result = UnorderedArray.parse(initials);
        // same value twice
        assertEquals(3,result.size());
        assertTrue(result.getItems().contains(new SimpleValue("value1")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullElement () throws Exception {
        String [] initials = new String [] { "value1", null};
        UnorderedArray.parse(initials);
    }


}
