package schedule;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This class represents a schedule event (i.e. a class or custom weekly event). 
 */
public class ScheduleEvent {
	
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("hhmma");

	private int id;
	private String name;
	private String location;
	private LocalTime start;
	private LocalTime end;
	private Day day;

	// TODO Values should be validated when ScheduleEvent is created
	public ScheduleEvent(int id, String name, String location, String start,
			String end, Day day) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.start = LocalTime.parse(start, DATE_FORMAT);
		this.end = LocalTime.parse(end, DATE_FORMAT);
		this.day = day;
	}

	/**
	 * Returns a string representing the event's start/end times. 
	 */
	public String getTimeString() {
		return start.toString("hh:mm") + " - " + end.toString("hh:mm");
	}
	
	/**
	 * Returns the event's duration in minutes. For determining
	 * height in schedule layout.
	 */
	public int getDuration() {
		// Minutes since midnight
		int startMinutes = getStartMinutes();
		int endMinutes = (end.getHourOfDay() * 60) + end.getMinuteOfHour();
		
		return endMinutes - startMinutes;
	}
	
	/**
	 * Returns number of minutes past midnight the start time is.
	 * For determining margin in schedule layout.
	 */
	public int getStartMinutes() {
		return (start.getHourOfDay() * 60) + start.getMinuteOfHour();
	}


	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public LocalTime getStart() {
		return start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public Day getDay() {
		return day;
	}

}
