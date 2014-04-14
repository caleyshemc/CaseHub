package schedule.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.LocalTime;

import schedule.Day;
import schedule.ScheduleEvent;

import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class CalendarHelper {
	
	private int start_year;
	private int start_month;
	private int start_day;
	private int end_year;
	private int end_month;
	private int end_day;

	public void addEvents(ArrayList<ScheduleEvent> events) {
		
		// TODO take user input for start/end dates
		
		
		
	}
	
	private void addEvent(ScheduleEvent event) {
		
		// Get event info
		String name = event.getName();
		String location = event.getLocation();
		LocalTime start = event.getStart();
		LocalTime end = event.getEnd();
		Day day = event.getDay();
				
		// Set begin/end times for first occurrence
		// TODO calculate first day of proper weekday from start_day
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(start_year, start_month, start_day, start.getHourOfDay(), start.getMinuteOfHour());
		Calendar endTime = Calendar.getInstance();
		endTime.set(start_year, start_month, start_day, end.getHourOfDay(), end.getMinuteOfHour());
			
		// Set recurrence preferences
		String recurrence = "FREQ=WEEKLY;";
		recurrence += "BYDAY=" + day.toString().substring(0,1);
		recurrence += "UNTIL=" + end_year + end_month + end_day;
				
		// Create intent to send to calendar
		Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
		        .putExtra(Events.TITLE, event.getName())
		        .putExtra(Events.EVENT_LOCATION, event.getLocation())
		        .putExtra(Events.RRULE, recurrence)
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		
		/* TODO
		startActivity(intent);
		*/
	}
	
}
