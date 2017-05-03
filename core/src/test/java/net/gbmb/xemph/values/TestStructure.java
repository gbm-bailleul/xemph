package net.gbmb.xemph.values;

import net.gbmb.xemph.Name;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Guillaume Bailleul on 02/05/2017.
 */
public class TestStructure {

    public Structure structure;



    @Before
    public void before () {
        structure = new Structure();
        structure.add(new Name("ns","n1"),new SimpleValue("value1"));
        structure.add(new Name("ns","n2"),new SimpleValue("value2"));
    }


    @Test(expected = UnsupportedOperationException.class)
    public void cannotModifyItems () {
        structure.getFields().put(new Name("ns","n3"),new SimpleValue("New value"));
    }

}
