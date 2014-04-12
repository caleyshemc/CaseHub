package schedule;

import java.util.ArrayList;

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
		values.put(ScheduleEventEntry.COL_EVENT_START, event.getStart().toString(ScheduleEvent.DATE_FORMAT));
		values.put(ScheduleEventEntry.COL_EVENT_END, event.getEnd().toString(ScheduleEvent.DATE_FORMAT));
		values.put(ScheduleEventEntry.COL_EVENT_DAY, event.getDay().toString());
		
		// Insert values into database
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
		db.insert(ScheduleEventEntry.TABLE_NAME, null, values);
		
	}

	/**
	 * Displays entire schedule as it exists in the database
	 */
	// TODO deal with duplicates!
	// Database should not allow copies (same event id, same start/end time, same day)
	public ArrayList<ScheduleEvent> getSchedule() {
		
		// Retrieve database
		SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns to retrieve
		String[] projection = {
			ScheduleEventEntry.COL_EVENT_ID,
			ScheduleEventEntry.COL_EVENT_NAME,
			ScheduleEventEntry.COL_EVENT_LOCATION,
			ScheduleEventEntry.COL_EVENT_START,
			ScheduleEventEntry.COL_EVENT_END,
			ScheduleEventEntry.COL_EVENT_DAY
			};
		
		// Query for all schedule events in table
		Cursor c = db.query(
				ScheduleEventEntry.TABLE_NAME,  // The table to query
			    projection,    	// The columns to return
			    null,          	// The columns for the WHERE clause
			    null, 			// The values for the WHERE clause
			    null,          	// don't group the rows
			    null,          	// don't filter by row groups
			    null	     	// The sort order
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
		int name_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_NAME);
		int loc_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_LOCATION);
		int start_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_START);
		int end_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_END);
		int day_index = c.getColumnIndexOrThrow(ScheduleEventEntry.COL_EVENT_DAY);
		
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
			
			ScheduleEvent event = new ScheduleEvent(id, name, location, start, end, Day.valueOf(day));
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

}

