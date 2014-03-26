package schedule;

import org.joda.time.LocalTime;

/**
 * This class represents a schedule event (i.e. a class or custom weekly event).
 */
public class ScheduleEvent {

	private String name;
	private String location;
	private LocalTime start;
	private LocalTime end;
	private Day[] days;

	public ScheduleEvent(String name, String location, LocalTime start,
			LocalTime end, Day[] days) {
		this.name = name;
		this.location = location;
		this.start = start;
		this.end = end;
		this.days = days;
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
		int startMinutes = (start.getHourOfDay() * 60) + start.getMinuteOfHour();
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

	public Day[] getDays() {
		return days;
	}

}
