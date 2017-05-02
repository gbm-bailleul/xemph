package net.gbmb.xemph.values;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Guillaume Bailleul on 02/05/2017.
 */
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

}
