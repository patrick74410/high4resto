package fr.high4technology.high4resto.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;

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

    public static String generateTextForSpeach(ItemCarte item)
    {
        StringBuilder text=new StringBuilder();
        StringBuilder ajout=new StringBuilder();
        item.getOptions().forEach(option->{
            StringBuilder options=new StringBuilder();
            StringBuilder choixx=new StringBuilder();
            options.append("Avec "+option.getLabel()+":");
            option.getOptions().forEach(choix->{
                if(choix.isSelected())
                {
                    choixx.append("-"+choix.getLabel()+".");
                }
            });
            if(choixx.length()>0)
            {
                options.append(choixx.toString());
                ajout.append(options.toString());
            }
        });
        text.append(ajout.toString());
        return text.toString();
    }

}
