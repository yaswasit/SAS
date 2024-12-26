package com.tsspdcl.sas.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateComparison {
	
	public static void main(String args[]) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateInString = "01-05-2024 10:20:56";
		Date date = null;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //        DateAndCalendar obj = new DateAndCalendar();
		
		//2. Test - Convert Date to Calendar
		Calendar calendar = dateToCalendar(date);
		System.out.println("Given Date Before adding..."+calendar.getTime());
		Date currentDate = calendar.getTime();
		System.out.println("Given Date in Date format..."+sdf.format(currentDate));
		calendar.add(Calendar.HOUR_OF_DAY, 6);
		System.out.println("after adding hours..."+calendar.getTime());
		Date afterDate = calendar.getTime();
		System.out.println("Given Date in Date format after adding 6 hours..."+sdf.format(afterDate));
		
		//3. Test - Convert Calendar to Date
		Date newDate = calendarToDate(calendar);
		System.out.println(newDate);
		
			
		Calendar calendar1 = Calendar.getInstance();
	    Date today = calendar1.getTime();
		
	    System.out.println("Current Date & Time: " + sdf.format(today));
	   
	    //calendar1.add(Calendar.HOUR_OF_DAY, 4);
	    //Date addHours = calendar1.getTime();
	    //System.out.println("Time after 4 hours: " + sdf.format(addHours));
	
	}
	
	//Convert Date to Calendar
		private static Calendar dateToCalendar(Date date) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			return calendar;

		}

		//Convert Calendar to Date
		private static Date calendarToDate(Calendar calendar) {
			return calendar.getTime();
		}


}