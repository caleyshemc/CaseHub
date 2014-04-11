package schedule;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import casehub.CaseHubContract.ScheduleEventEntry;
import casehub.MainActivity;

/**
 * Retrieves schedule information from database.
 */
public class GetScheduleTask extends AsyncTask<String, Void, ArrayList<ScheduleEvent>> {
	
	/*
	 * Displays entire schedule as it exists in the database
	 */
	private ArrayList<ScheduleEvent> displaySchedule() {
		
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
		
		c.moveToFirst();
		
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

	@Override
	protected ArrayList<ScheduleEvent> doInBackground(String... arg0) {
		return displaySchedule();
	}

}

