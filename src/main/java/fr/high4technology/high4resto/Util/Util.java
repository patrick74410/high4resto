package fr.high4technology.high4resto.Util;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class Util {
    public static String getTimeNow()
    {
        Locale locale1 = Locale.FRANCE;
        TimeZone tz1 = TimeZone.getTimeZone("Europe/Paris");
        Calendar cal = GregorianCalendar.getInstance(tz1,locale1);
        if (tz1.inDaylightTime(new Date())) cal.add(Calendar.HOUR,1);
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal);
    }
    public static Date getDateNow()
    {
        Locale locale1 = Locale.FRANCE;
        TimeZone tz1 = TimeZone.getTimeZone("Europe/Paris");
        Calendar cal = GregorianCalendar.getInstance(tz1,locale1);
        if (tz1.inDaylightTime(new Date())) cal.add(Calendar.HOUR,1);
        return cal.getTime();
    }    
}
