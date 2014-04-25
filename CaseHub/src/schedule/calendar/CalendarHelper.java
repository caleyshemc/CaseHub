package schedule.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.LocalTime;

import schedule.Day;
import schedule.ScheduleEvent;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

public class CalendarHelper {
	
	private Context context;
	
	private int start_year;
	private int start_month;
	private int start_day;
	private int end_year;
	private int end_month;
	private int end_day;
	
	public CalendarHelper(Context context) {
		this.context = context;
	}

	public void exportEvents(ArrayList<ScheduleEvent> events, Calendar startDate, Calendar endDate) {
		
		start_year = startDate.get(Calendar.YEAR);
		start_month = startDate.get(Calendar.MONTH);
		start_day = startDate.get(Calendar.DAY_OF_MONTH);
		
		end_year = endDate.get(Calendar.YEAR);
		end_month = endDate.get(Calendar.MONTH);
		end_day = endDate.get(Calendar.DAY_OF_MONTH);
				
		for (ScheduleEvent event : events) {
			addEvent(event);
		}
		
	}
	
	private void addEvent(ScheduleEvent event) {
		
		// Get event info
		String name = event.getName();
		String location = event.getLocation();
		LocalTime start = event.getStart();
		LocalTime end = event.getEnd();
		Day day = event.getDay();
				
		// Set begin/end times for first occurrence
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(start_year, start_month, start_day, start.getHourOfDay(), start.getMinuteOfHour());
		Calendar endTime = Calendar.getInstance();
		endTime.set(start_year, start_month, start_day, end.getHourOfDay(), end.getMinuteOfHour());
				
		// Set recurrence preferences
		String recurrence = "FREQ=WEEKLY;";
		recurrence += "BYDAY=" + day.toString().substring(0,2) + ";";
		recurrence += "UNTIL=" + end_year + end_month + end_day;
				
		// Create intent to send to calendar
		Intent intent = new Intent(Intent.ACTION_INSERT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
		        .putExtra(Events.TITLE, name)
		        .putExtra(Events.EVENT_LOCATION, location)
		        .putExtra(Events.RRULE, recurrence)
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		
		context.startActivity(intent);
	}
	
}
