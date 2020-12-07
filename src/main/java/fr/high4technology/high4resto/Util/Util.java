package fr.high4technology.high4resto.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

public class Util {
    private static java.util.Random rand = new java.util.Random();
	private static String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    private static Set<String> identifiers = new HashSet<String>();
    private static final BigInteger PRIME32 = new BigInteger("01000193",         16);
    private static final BigInteger MOD32   = new BigInteger("2").pow(32);
    private static final BigInteger INIT32  = new BigInteger("811c9dc5",         16);

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

    public static String hash(String text) {
        var data=text.getBytes();
        BigInteger hash = INIT32;

        for (byte b : data) {
          hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
          hash = hash.multiply(PRIME32).mod(MOD32);
        }

        return hash.toString();
      }
}
