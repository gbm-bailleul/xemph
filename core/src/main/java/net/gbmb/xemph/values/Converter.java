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