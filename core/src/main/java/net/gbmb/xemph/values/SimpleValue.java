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
import net.gbmb.xemph.Value;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * String wrapper (and all subtypes)
 */
public class SimpleValue extends Value {

    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ssX";

    private static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };

    private String content;

    public SimpleValue(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return getContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleValue that = (SimpleValue) o;

        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }

    public Calendar asDate () {
        return convert(this,Calendar.class);
    }

    public boolean asBoolean () {
        return convert(this, Boolean.class);
    }

    public String asString () {
        return content;
    }

    public int asInteger () {
        return Integer.parseInt(this.content);
    }

    public long asLong () {
        return Long.parseLong(this.content);
    }

    public BigInteger asBigInteger () {
        return new BigInteger(this.content);
    }

    public float asFloat () {
        return Float.parseFloat(this.content);
    }

    public Double asDouble () {
        return Double.parseDouble(this.content);
    }

    public BigDecimal asBigDecimal () {
        return new BigDecimal(this.content);
    }

    public MimeType asMIME () throws MimeTypeParseException{
        return new MimeType(this.content);
    }

    public URI asURI () {
        return URI.create(this.content);
    }


    public UUID asUUID () throws InvalidTypeConvertException {
        if (content.startsWith("uuid:")) {
            return UUID.fromString(content.substring(5));
        } else {
            throw new InvalidTypeConvertException("Cannot convert to UUID: "+content);
        }
    }



    /**
     * Convert a SimpleValue to the expected class instance
     * @param original
     * @param target can be Calendar.class or Boolean.class
     * @param <T>
     * @return
     */
    public static  <T> T convert(SimpleValue original, Class<T> target) {
        if (target==Calendar.class) {
            try {
                Date date = dateFormater.get().parse(original.getContent());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                return target.cast(cal);
            } catch (ParseException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else if (target == Boolean.class) {
            return target.cast(Boolean.valueOf(original.getContent()));
        } else {
            throw new IllegalArgumentException("Not yet implemented convert from " + original.getClass() + " to " + target);
        }

    }

    /**
     * Create a SimpleValue based on the value parameter. When no converter is defined for class of the value
     * parameter, the toString method is used.
     * @param value the value to parse
     * @return
     */
    public static SimpleValue parse(Object value) {
        if (value instanceof String) {
            return new SimpleValue((String)value);
        } else if (value instanceof Date) {
            return new SimpleValue(dateFormater.get().format((Date)value));
        } else if (value instanceof Calendar) {
            return new SimpleValue(dateFormater.get().format(((Calendar)value).getTime()));
        } else {
            // default
            return new SimpleValue(value.toString());
        }
    }

}
