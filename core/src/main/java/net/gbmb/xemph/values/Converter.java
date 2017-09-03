package net.gbmb.xemph.values;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guillaume Bailleul on 31/08/2017.
 */
public class Converter {

    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ssX";

    private static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };

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

}