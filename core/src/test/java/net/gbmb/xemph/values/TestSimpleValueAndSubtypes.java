package net.gbmb.xemph.values;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Guillaume Bailleul on 02/05/2017.
 */
public class TestSimpleValueAndSubtypes {

    private static final String CONTENT = "mycontent";


    @Test
    public void checkURI () {
        URIValue value = new URIValue(CONTENT);
        assertEquals(CONTENT,value.getContent());
    }

}
