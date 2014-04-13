package schedule;

import java.util.ArrayList;

import org.joda.time.LocalTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import casehub.CaseHubContract.ScheduleEventEntry;
import casehub.MainActivity;

/**
 * Saves and retrieves schedule information from database.
 */
public class ScheduleDBHelper {

	/**
	 * Add an event to the schedule.
	 */
	public void addEvent(ScheduleEvent event) {

		// Create map of event values
		ContentValues values = new ContentValues();
		values.put(ScheduleEventEntry.COL_EVENT_ID, event.getId());
		values.put(ScheduleEventEntry.COL_EVENT_NAME, event.getName());
		values.put(ScheduleEventEntry.COL_EVENT_LOCATION, event.getLocation());
		values.put(ScheduleEventEntry.COL_EVENT_START, event.getStart()
				.toString(ScheduleEvent.DATE_FORMAT));
		values.put(ScheduleEventEntry.COL_EVENT_END,
				event.getEnd().toString(ScheduleEvent.DATE_FORMAT));
		values.put(ScheduleEventEntry.COL_EVENT_DAY, event.getDay().toString());

		// Insert values into database
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
		db.insert(ScheduleEventEntry.TABLE_NAME, null, values);

	}

	/**
	 * Returns list of schedule events as they exist in the database.
	 */
	// TODO deal with duplicates!
	// Database should not allow copies (same event id, same start/end time,
	// same day)
	public ArrayList<ScheduleEvent> getSchedule() {

		// Retrieve database
		SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns to retrieve
		String[] projection = { ScheduleEventEntry.COL_EVENT_ID,
				ScheduleEventEntry.COL_EVENT_NAME,
				ScheduleEventEntry.COL_EVENT_LOCATION,
				ScheduleEventEntry.COL_EVENT_START,
				ScheduleEventEntry.COL_EVENT_END,
				ScheduleEventEntry.COL_EVENT_DAY };

		// Query for all schedule events in table
		Cursor c = db.query(ScheduleEventEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		/* Display each event */

		ArrayList<ScheduleEvent> events = new ArrayList<ScheduleEvent>();

		int id;
		String name;
		String location;
		String start;
		String end;
		String day;

		// Grab column indices
		int id_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_ID);
		int name_index = c
				.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_NAME);
		int loc_index = c
				.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_LOCATION);
		int start_index = c
				.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_START);
		int end_index = c
				.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_END);
		int day_index = c
				.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_DAY);

		// If no entries found
		if (!c.moveToFirst()) {
			return events;
		}

		do {

			id = c.getInt(id_index);
			name = c.getString(name_index);
			location = c.getString(loc_index);
			start = c.getString(start_index);
			end = c.getString(end_index);
			day = c.getString(day_index);

			// TODO set colors here?

			ScheduleEvent event = new ScheduleEvent(id, name, location, start,
					end, Day.valueOf(day));
			events.add(event);

		} while (c.moveToNext());

		return events;
	}

	/**
	 * Deletes all events from the database.
	 */
	public void clearSchedule() {
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
		db.delete(ScheduleEventEntry.TABLE_NAME, null, null);
	}

	/**
	 * Gets the starting hour of the earliest event. For setting visible hours
	 * in the layout.
	 */
	public int getEarliestHour() {

		ArrayList<LocalTime> times = getStartTimes();
		
		if (times.isEmpty()) {
			return ScheduleFragment.FIRST_HOUR;
		}

		// Find earliest time
		LocalTime earliest = new LocalTime(23, 59);
		for (LocalTime time : times) {
			if (time.getHourOfDay() < earliest.getHourOfDay()) {
				earliest = time;
			}
		}

		return earliest.getHourOfDay();
	}

	/**
	 * Gets the ending hour of the latest event. For setting visible hours in
	 * the layout.
	 */
	public int getLatestHour() {

		ArrayList<LocalTime> times = getEndTimes();
		
		if (times.isEmpty()) {
			return ScheduleFragment.LAST_HOUR;
		}
		
		// Find latest time
		LocalTime latest = new LocalTime(0, 0);
		for (LocalTime time : times) {
			if (time.getHourOfDay() > latest.getHourOfDay()) {
				latest = time;
			}
		}

		return latest.getHourOfDay();

	}

	/**
	 * Returns list of event start times for autosilent feature.
	 */
	public ArrayList<LocalTime> getStartTimes() {
		ArrayList<LocalTime> startTimes = new ArrayList<LocalTime>();

		// Retrieve database
		SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns to retrieve
		String[] projection = { ScheduleEventEntry.COL_EVENT_START };

		// Query for all schedule events in table
		Cursor c = db.query(ScheduleEventEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		// If no entries found
		if (!c.moveToFirst()) {
			return startTimes;
		}

		// Retrieve and parse into LocalTime objects
		String start;
		LocalTime startTime;
		ArrayList<LocalTime> times = new ArrayList<LocalTime>();
		int start_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_START);

		do {
			start = c.getString(start_index);
			startTime = LocalTime.parse(start, ScheduleEvent.DATE_FORMAT);
			times.add(startTime);
		} while (c.moveToNext());

		return startTimes;
	}

	/**
	 * Returns list of event end times for autosilent feature.
	 */
	public ArrayList<LocalTime> getEndTimes() {
		ArrayList<LocalTime> endTimes = new ArrayList<LocalTime>();

		// Retrieve database
		SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns to retrieve
		String[] projection = { ScheduleEventEntry.COL_EVENT_END };

		// Query for all schedule events in table
		Cursor c = db.query(ScheduleEventEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		
		// If no entries found
		if (!c.moveToFirst()) {
			return endTimes;
		}
		
		// Retrieve and parse into LocalTime objects
		String end;
		LocalTime endTime;
		ArrayList<LocalTime> times = new ArrayList<LocalTime>();
		int end_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_END);
		
		do {
			end = c.getString(end_index);
			endTime = LocalTime.parse(end, ScheduleEvent.DATE_FORMAT);
			times.add(endTime);
		} while (c.moveToNext());

		return endTimes;
	}

}
