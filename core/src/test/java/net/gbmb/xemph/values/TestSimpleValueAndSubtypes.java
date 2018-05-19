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

import net.gbmb.xemph.InvalidTypeConvertException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestSimpleValueAndSubtypes {

    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ssX";

    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

//    private static final String CONTENT = "mycontent";

    public SimpleValue prepare (Object CONTENT) throws Exception {
        SimpleValue sv1 = SimpleValue.parse(CONTENT);
        SimpleValue sv2 = SimpleValue.parse(CONTENT);
        assertNotEquals(CONTENT,sv1);
        assertEquals(sv1,sv2);
        return sv1;
    }


    @Test
    public void testString () throws Exception {
        String content = "mycontent";
        SimpleValue sv1 = prepare(content);
        assertEquals(content.toString(),sv1.getContent());
        assertEquals(content,sv1.toString());
        assertEquals(content,sv1.asString());
    }

    @Test
    public void testInteger () throws Exception {
        Integer content = new Integer(14);
        SimpleValue sv1 = prepare(content);
        assertEquals(content.toString(),sv1.getContent());
        assertEquals(content.toString(),sv1.toString());
        assertEquals(content.toString(),sv1.asString());
        assertEquals(content.intValue(),sv1.asInteger());
    }

    @Test
    public void testLong () throws Exception {
        Long content = new Long(14);
        SimpleValue sv1 = prepare(content);
        assertEquals(content.toString(),sv1.getContent());
        assertEquals(content.toString(),sv1.toString());
        assertEquals(content.toString(),sv1.asString());
        assertEquals(content.longValue(),sv1.asLong());
        assertEquals(content.intValue(),sv1.asInteger());
    }

    @Test
    public void testBooleanTrue () throws Exception {
        Boolean content = Boolean.TRUE;
        SimpleValue sv1 = prepare(content);
        assertEquals(content.toString(),sv1.getContent());
        assertEquals(content.toString(),sv1.toString());
        assertEquals(content.toString(),sv1.asString());
        assertEquals(content,sv1.asBoolean());
    }

    @Test
    public void testBooleanFalse () throws Exception {
        Boolean content = Boolean.FALSE;
        SimpleValue sv1 = prepare(content);
        assertEquals(content.toString(),sv1.getContent());
        assertEquals(content.toString(),sv1.toString());
        assertEquals(content.toString(),sv1.asString());
        assertEquals(content,sv1.asBoolean());
    }

    @Test
    public void testCalendar () throws Exception {
        Calendar content = Calendar.getInstance();
        content.set(Calendar.MILLISECOND,0);
        String string = sdf.format(content.getTime());
        SimpleValue sv1 = prepare(content);
        assertEquals(string,sv1.getContent());
        assertEquals(string,sv1.toString());
        assertEquals(string,sv1.asString());
        assertEquals(content,sv1.asDate());
    }

    @Test
    public void testDate () throws Exception {
        Calendar content = Calendar.getInstance();
        content.set(Calendar.MILLISECOND,0);
        Date date = content.getTime();
        String string = sdf.format(date);
        SimpleValue sv1 = prepare(date);
        assertEquals(string,sv1.getContent());
        assertEquals(string,sv1.toString());
        assertEquals(string,sv1.asString());
        assertEquals(date,sv1.asDate().getTime());
    }

    @Test
    public void testUUID () throws Exception {
        UUID uuid = UUID.randomUUID();
        SimpleValue sv = SimpleValue.parse(uuid);
        assertEquals(SimpleValue.UUID_PREFIX+uuid.toString(),sv.getContent());
        assertEquals(uuid,sv.asUUID());
    }

    @Test(expected = InvalidTypeConvertException.class)
    public void testInvalidUUID () throws Exception {
        UUID uuid = UUID.randomUUID();
        SimpleValue sv = SimpleValue.parse(uuid.toString());
        sv.asUUID();
//        assertEquals(SimpleValue.UUID_PREFIX+uuid.toString(),sv.getContent());
//        assertEquals(uuid,sv.asUUID());
    }


}
