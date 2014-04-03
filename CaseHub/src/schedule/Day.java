package schedule;

import java.util.Locale;

/**
 * Represents a day of the week.
 * 
 * Currently only includes weekdays, but will add user option to include
 * weekends in the future.
 */
public enum Day {
	MON, TUES, WED, THURS, FRI;

	/**
	 * Get full string name of day, e.g. "friday"
	 */
	public String getString() {
		if (this == WED) {
			return "wednesday";
		} else {
			return this.toString().toLowerCase(Locale.getDefault()) + "day";
		}
	}

	/**
	 * Gets weekday codes defined by Scheduler's day.php parameters
	 */
	public String getCode() {
		if (this == THURS) {
			return "R";
		} else {
			return this.toString().substring(0, 1);
		}
	}

}
