package net.gbmb.xemph.values;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Created by Guillaume Bailleul on 02/05/2017.
 */
public class TestOrderedArray {

    private OrderedArray<SimpleValue> array = new OrderedArray<>();

    public TestOrderedArray()  {
        array.addItem(new SimpleValue("value1"));
        array.addItem(new SimpleValue("value2"));
        array.addItem(new SimpleValue("value3"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyItems () {
        array.getItems().add(new SimpleValue("value"));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void cannotRetrieveNonExistingItem () {
        array.getItem(3);
    }

    @Test
    public void canHaveSameElementMultipleTime () {
        array.addItem(array.getItem(0));
        assertEquals(4,array.getItems().size());
        assertEquals(array.getItem(0),array.getItem(3));
    }

}
