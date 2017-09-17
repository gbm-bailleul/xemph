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

package net.gbmb.xemph;

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
