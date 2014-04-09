package schedule;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.joda.time.LocalTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import casehub.CaseHubContract.ScheduleEventEntry;
import casehub.MainActivity;

import com.casehub.R;

public class ScheduleFragment extends Fragment {
	
	/**
	 * Sets first and last hours displayed in Schedule view; used to determine
	 * placement of events.
	 * 
	 * To change the first/last hours, both these constants and the layout must
	 * be updated.
	 */
	public static final int FIRST_HOUR = 7;
	public static final int LAST_HOUR = 21;	
	
	/**
	 * Preferences filename to track whether user has logged in
	 */
	public static final String LOGIN_PREF = "LoginPrefsFile";
	public static final String LOGGED_IN = "hasLoggedIn";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_schedule, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
				
		// Check if user has logged in previously
		SharedPreferences settings = getActivity().getSharedPreferences(LOGIN_PREF, 0);
		boolean hasLoggedIn = settings.getBoolean(LOGGED_IN, false);

		if (!hasLoggedIn) {
			
			// Show login dialog
			DialogFragment loginDialog = new LoginDialogFragment();
			loginDialog.show(getFragmentManager(), "login");
			
		} else {
			displaySchedule();
		}
		
		placeTimeLine();
		
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	/**
	 * Places the line indicating the current time.
	 * TODO: Call whenever fragment is opened.
	 */
	public void placeTimeLine() {
		LocalTime now = LocalTime.now();
		int minutes = (now.getHourOfDay() * 60) + now.getMinuteOfHour();
		LinearLayout timeLine = (LinearLayout) getActivity().findViewById(R.id.current_time);
		
		// If current time within schedule hours
		if (FIRST_HOUR*60 < minutes && minutes < LAST_HOUR*60) {
			
			// Set timeline margin
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeLine.getLayoutParams();
			params.topMargin = dpToPixels(minutes - (FIRST_HOUR*60));
			
		} else {
			// Hide timeline
			timeLine.setVisibility(LinearLayout.GONE);
		}
		
	}
	
	/**
	 * Parses schedule from HTML into list of ScheduleEvents
	 */
	public ArrayList<ScheduleEvent> parseSchedule(String html) {
				
		ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<ScheduleEvent>();
		Document doc = Jsoup.parse(html);

		// For each day of the week
		for (Day day : Day.values()) {
			
			// Select each event in this day
			Element div = doc.getElementById(day.toString());
			Elements events = div.select(".event");
			
			// Create ScheduleEvents
			for (Element event : events) {
				
				// Get raw event info
				String name = event.select(".eventname").first().text();
				String times = event.select(".timespan").first().text();
				String location = event.select(".location").first().text();
				
				// Get event ID by extracting digits from 'onclick' attribute
				String idString = event.attr("onclick");
				idString = idString.replaceAll("\\D+","");
				int id = Integer.parseInt(idString);
				
				// Extract start/end times
				String[] split = times.split("-");
				String start = split[0] + "m";
				String end = split[1] + "m";
								
				ScheduleEvent newEvent =  new ScheduleEvent(id, name, location, start, end, day);
				
				scheduleEvents.add(newEvent);
				
			}
			
		}
		
		return scheduleEvents;
		
	}
	
	/**
	 * Add an event to the schedule.
	 */
	public void addEvent(ScheduleEvent event) {

		// TODO Values should be validated when ScheduleEvent is created
		
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
		long newRowId = db.insert(ScheduleEventEntry.TABLE_NAME, null, values);
		
	}
	
	/*
	 * Displays entire schedule as it exists in the database
	 */
	private void displaySchedule() {
		
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
			displayEvent(event);
			
		} while (c.moveToNext());
		
		
		

	}
	
	/*
	 * Displays a single event.
	 */
	private void displayEvent(ScheduleEvent event) {
				
		int height = event.getDuration();
		int topMargin = event.getStartMinutes() - (FIRST_HOUR * 60);

		if (height < 1) {
			throw new InvalidParameterException("Error: Event duration must be at least 1 minute.");
		}
		if (topMargin < 0) {
			throw new InvalidParameterException("Error: Events cannot start before hour " + FIRST_HOUR);
		}
		if ((height + topMargin) > LAST_HOUR * 60) {
			throw new InvalidParameterException("Error: Events cannot end after hour " + LAST_HOUR);
		}

		// Grab event layout template
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.template_event_layout, null);

		// Set event layout template dimensions
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.height = dpToPixels(height - 1);
		params.setMargins(0, dpToPixels(topMargin), 0, 0);
		layout.setLayoutParams(params);

		// For each day of the week this event occurs, add to layout
		int layoutId;
		LinearLayout eventLayout;

		RelativeLayout parentLayout;

		// Clone layout by inflating template
		eventLayout = (LinearLayout) inflater.inflate(R.layout.template_event_layout, null);
		eventLayout.setLayoutParams(params);

		// Set event text values
		TextView name = (TextView) eventLayout.findViewWithTag("name");
		TextView time = (TextView) eventLayout.findViewWithTag("time");
		TextView location = (TextView) eventLayout.findViewWithTag("location");

		name.setText(event.getName());
		time.setText(event.getTimeString());
		location.setText(event.getLocation());

		// Get parent layout
		String day = event.getDay().getString();
		layoutId = getResources().getIdentifier(day, "id", this.getActivity().getPackageName());
		parentLayout = (RelativeLayout) getView().findViewById(layoutId);

		// Add new event layout to parent layout
		parentLayout.addView(eventLayout);
	}

	/*
	 * Converts dp to pixels, as dp cannot be set directly at runtime.
	 * Used for setting layout parameters.
	 */
	private int dpToPixels(int dp) {
		DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
		
		float fpixels = metrics.density * dp;
		int pixels = (int) (fpixels + 0.5f);
		
		return pixels;

	}
	
}

