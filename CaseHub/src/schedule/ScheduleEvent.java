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
