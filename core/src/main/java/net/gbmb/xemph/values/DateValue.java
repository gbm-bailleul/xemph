package net.gbmb.xemph.values;

import net.gbmb.xemph.Value;

import java.util.Calendar;

/**
 * Created by Guillaume Bailleul on 05/05/2017.
 */
public class DateValue extends Value {

    private Calendar date;

    public DateValue (Calendar date) {
        this.date = date;
    }

    public Calendar getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date.toString();
    }
}
