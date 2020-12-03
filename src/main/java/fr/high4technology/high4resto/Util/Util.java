package fr.high4technology.high4resto.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import java.text.SimpleDateFormat;

public class Util {
    private static java.util.Random rand = new java.util.Random();
	private static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    private static Set<String> identifiers = new HashSet<String>();
    public static String getTimeNow() {
        Locale locale1 = Locale.FRANCE;
        TimeZone tz1 = TimeZone.getTimeZone("Europe/Paris");
        Calendar cal = GregorianCalendar.getInstance(tz1, locale1);
        if (tz1.inDaylightTime(new Date()))
            cal.add(Calendar.HOUR, 1);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
        format.setTimeZone(tz1);
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE).format(cal.getTime());
    }

    public static Date getDateNow() {
        Locale locale1 = Locale.FRANCE;
        TimeZone tz1 = TimeZone.getTimeZone("Europe/Paris");
        Calendar cal = GregorianCalendar.getInstance(tz1, locale1);
        if (tz1.inDaylightTime(new Date()))
            cal.add(Calendar.HOUR, 1);
        return cal.getTime();
    }
    public static String randomIdentifier() {
		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = rand.nextInt(5) + 5;
			for (int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
    }

}
