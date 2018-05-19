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

import java.util.Arrays;

import static org.junit.Assert.*;


public class TestOrderedArray {

    private OrderedArray<SimpleValue> array = new OrderedArray<>();

    public void init()  {
        array.addItem(new SimpleValue("value1"));
        array.addItem(new SimpleValue("value2"));
        array.addItem(new SimpleValue("value3"));
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
    public void canHaveSameElementMultipleTime () {
        init();
        array.addItem(array.getItem(0));
        assertEquals(4,array.getItems().size());
        assertEquals(array.getItem(0),array.getItem(3));
    }

    @Test
    public void testPossibleParse () throws Exception {
        String [] initials = new String [] { "value1", "value2", "value3", "value1"};
        OrderedArray<SimpleValue> result = OrderedArray.parse(initials);
        assertEquals(4,result.size());
        assertEquals("value1",result.getItemAsSimpleValue(0).asString());
    }

    @Test
    public void testMultipleAdd () throws Exception {
        String [] initials = new String [] { "value1", "value2", "value3", "value1"};
        SimpleValue [] values = (SimpleValue [])Arrays.stream(initials).map(a -> new SimpleValue(a)).toArray(SimpleValue[]::new);

        OrderedArray<SimpleValue> result = new OrderedArray<>();
        result.addItems(values);
        assertEquals(4,result.size());
        assertEquals("value1",result.getItemAsSimpleValue(0).asString());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testParseWithNullElement () throws Exception {
        String [] initials = new String [] { "value1", null};
        OrderedArray<SimpleValue> result = OrderedArray.parse(initials);
    }

    @Test
    public void testStringRepresentation () throws Exception {
        init();
        String s = array.toString();
        assertEquals("[ value1, value2, value3 ]",s);
    }

}
