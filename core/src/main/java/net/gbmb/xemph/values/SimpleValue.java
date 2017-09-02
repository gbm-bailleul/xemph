package net.gbmb.xemph.values;

import net.gbmb.xemph.InvalidTypeConvertException;
import net.gbmb.xemph.Value;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Guillaume Bailleul on 18/10/2016.
 */
public class SimpleValue extends Value {

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

    public Calendar asDate () {
        return Converter.convert(this,Calendar.class);
    }

    public boolean asBoolean () {
        return Converter.convert(this, Boolean.class);
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
}
