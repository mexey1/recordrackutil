package com.recordrack.logic;

import javafx.util.StringConverter;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class containing methods for formatting date data
 */
public class DateUtil
{
    public static Date getCurrentDate()
    {
        return new Date(System.currentTimeMillis());
    }

    public static Timestamp getCurrentTimeStamp()
    {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String formatDate(LocalDate date,String pattern)
    {
        String dayOfWeek = date.getDayOfWeek().toString();
        String  month = date.getMonth().toString();
        String year = Integer.toString(date.getYear());
        String dayOfMonth = Integer.toString(date.getDayOfMonth());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(date);

        //return dayOfWeek+", "+month+" "+dayOfMonth+" "+year;
    }

    public static StringConverter<LocalDate> getDateConverter(String pattern)
    {
        StringConverter<LocalDate> converter = new StringConverter<LocalDate>()
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate object)
            {
                if(object!=null)
                    return formatter.format(object);
                else
                    return "";
            }

            @Override
            public LocalDate fromString(String string)
            {
                if(string!=null && string.length()>0)
                    return LocalDate.parse(string,formatter);
                else
                    return null;
            }
        };
        return converter;
    }

    public static String convertDateToStartOfDay(LocalDate date)
    {
       return date.toString()+" 00:00:00";
    }

    public static String convertDateToEndOfDay(LocalDate date)
    {
        return date.toString()+" 23:59:59";
    }
}
