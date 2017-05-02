package net.gbmb.xemph;

import com.sun.javaws.exceptions.InvalidArgumentException;
import junit.framework.TestCase;
import net.gbmb.xemph.values.SimpleValue;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Guillaume Bailleul on 02/05/2017.
 */
public class TestPacket {

    private Packet packet = new Packet();

    private Name name = new Name ("NE","NE");

    @Test
    public void simplePacketCreation () {
        assertNotNull(packet.getProperties());
        assertNull(packet.getTargetResource());
    }

    @Test
    public void retrieveUnexistingProperty () {
        assertFalse(packet.contains(name));
        assertNull(packet.getValue(name));
    }


    @Test(expected=IllegalArgumentException.class)
    public void cannotOverwriteProperty () {
        packet.addProperty(name,"my value");
        assertTrue(packet.contains(name));
        packet.addProperty(name,"value 2");
    }

    @Test
    public void canForceOverwriteProperty () {
        packet.addProperty(name,"my value");
        assertTrue(packet.contains(name));
        packet.addProperty(name,"value 2",true);
        assertEquals("value 2",packet.getValue(name).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreatePropertyWithNoName () {
        packet.addProperty(null,"value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotCreatePropertyWithNoValue () {
        packet.addProperty(name,(Value)null);
    }

}
