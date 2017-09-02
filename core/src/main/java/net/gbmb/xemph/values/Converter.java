package net.gbmb.xemph.values;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guillaume Bailleul on 31/08/2017.
 */
public class Converter {

/*
    public <T extends Value> T convert(Value original, Class<T> target) {
        throw new IllegalArgumentException("Not implemented convert from " + original.getClass() + " to " + target);
    }
*/

    public static  <T> T convert(SimpleValue original, Class<T> target) {
        if (target==Calendar.class) {
            // TODO optimize (multi thread)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssX");
            try {
                Date date = sdf.parse(original.getContent());
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

}