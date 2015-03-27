package ru.mobigear.mobigearinterview.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eugene on 3/25/15.
 */
public class Formatter {
    public static String formatDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("d MMMM y, kk:mm", Locale.getDefault());
        return format.format(date);
    }

    public static Date dateFrom(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
