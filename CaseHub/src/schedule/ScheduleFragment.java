package schedule;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
			/* Display schedule from database 
			try {
				
				ArrayList<ScheduleEvent> events = new ScheduleDBTask().execute().get();
				
				for (ScheduleEvent event : events) {
					displayEvent(event);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
		placeTimeLine();
		
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    MenuItem item = menu.add(0, 1, 10, R.string.schedule_refresh);
	    item.setIcon(R.drawable.ic_action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case 1:
	        	// Show login dialog
				DialogFragment loginDialog = new LoginDialogFragment();
				loginDialog.show(getFragmentManager(), "login");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
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
	 * Displays a schedule event.
	 */
	public void displayEvent(ScheduleEvent event) {
				
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

