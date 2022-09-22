package org.example.groups;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
	static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);

	public static long timeInMillis(Item it ) throws ParseException {
		return formatter.parse(it.getTime()).getTime();
	}
	
	public static String toDateString(long timeMillis) {
		return new Date(timeMillis).toString();
	}
	
	public static long periodStart(long t) {
		return t - (t%Window.PERIOD);
	}
}
