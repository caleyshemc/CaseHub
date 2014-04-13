package schedule;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.joda.time.LocalTime;

import schedule.autosilent.AutoSilentDialog;
import schedule.login.LoginDialog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import com.casehub.R;

public class ScheduleFragment extends Fragment {
	
	/**
	 * Sets earliest/latest times displayed in Schedule view.
	 * 
	 * To change the first/last hours, both these constants and the layout must
	 * be updated.
	 * 
	 * Uses 24-hour clock.
	 */
	public static final int FIRST_HOUR = 7;
	public static final int LAST_HOUR = 21;
	
	// Currently displayed first/last hours
	// For setting event placement
	private int current_first_hour = FIRST_HOUR;
	private int current_last_hour = LAST_HOUR;
	
	/**
	 * ActionBar item IDs
	 */
	public static final int REFRESH_ID = 1;
	public static final int SILENT_ID = 2;
	
	/**
	 * SharedPreferences fields and filenames
	 */
	public static final String LOGGED_IN_PREF = "LoginPrefsFile";
	public static final String LOGGED_IN = "hasLoggedIn";
	public static final String SILENT_PREF = "AutoSilentPrefsFile";
	public static final String SILENT = "autoSilentSetting";
	public static final int SILENT_ON = 0;
	public static final int SILENT_VIBRATE = 1;
	public static final int SILENT_OFF = 2;
	
	private ScheduleDBHelper dbHelper;
	private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_schedule, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		dbHelper = new ScheduleDBHelper();
				
		// Check if user has logged in previously
		SharedPreferences settings = getActivity().getSharedPreferences(LOGGED_IN_PREF, 0);
		boolean hasLoggedIn = settings.getBoolean(LOGGED_IN, false);

		if (!hasLoggedIn) {
			
			// Show login dialog
			DialogFragment loginDialog = new LoginDialog();
			loginDialog.show(getFragmentManager(), "login");
			
		} else {
			
			// Display schedule from database
			ArrayList<ScheduleEvent> events = dbHelper.getSchedule();
			displayEvents(events);
			
		}
		
		placeTimeLine();
		
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Show refresh button
	    MenuItem refreshButton = menu.add(0, REFRESH_ID, 10, R.string.schedule_refresh);
	    refreshButton.setIcon(R.drawable.ic_action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    
	    // Get autosilent setting
	    SharedPreferences settings = getActivity().getSharedPreferences(SILENT_PREF, 0);
		int autoSilentSetting = settings.getInt(SILENT, SILENT_OFF);
		
		// Show autosilent button
	    MenuItem silentButton = menu.add(0, SILENT_ID, 20, R.string.schedule_silent);
	    
	    if (autoSilentSetting == SILENT_OFF) {
	    	silentButton.setIcon(R.drawable.ic_action_volume_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    } else {
	    	silentButton.setIcon(R.drawable.ic_action_volume_muted).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    }
	}
	
	/**
	 * Called when ActionBar button is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case REFRESH_ID:
				DialogFragment loginDialog = new LoginDialog();
				loginDialog.show(getFragmentManager(), "login");
	            return true;
	            
	        case SILENT_ID:
	        	DialogFragment silentDialog = new AutoSilentDialog();
	        	silentDialog.show(getFragmentManager(), "login");
	        	return true;
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Places the line indicating the current time.
	 */
	public void placeTimeLine() {
		LocalTime now = LocalTime.now();
		int minutes = (now.getHourOfDay() * 60) + now.getMinuteOfHour();
		LinearLayout timeLine = (LinearLayout) getActivity().findViewById(R.id.current_time);
		
		// If current time within schedule hours
		if (current_first_hour*60 < minutes && minutes < current_last_hour*60) {
			
			// Set timeline margin
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeLine.getLayoutParams();
			params.topMargin = dpToPixels(minutes - (current_first_hour*60));
			
		} else {
			// Hide timeline
			timeLine.setVisibility(LinearLayout.GONE);
		}
		
	}
	
	/**
	 * Add events to the database and display in schedule.
	 */
	public void addEvents(ArrayList<ScheduleEvent> events) {
				
		// Add events to database
		for (ScheduleEvent event : events) {
			dbHelper.addEvent(event);
		}
		
		// Display events
		displayEvents(events);
		
	}
	
	/*
	 * Displays events in the schedule.
	 */
	private void displayEvents(ArrayList<ScheduleEvent> events) {
		
		setVisibleHours();
		
		for (ScheduleEvent event : events) {
			displayEvent(event);
		}
		
	}
	
	/**
	 * Deletes all schedule information from the event table
	 */
	public void clearSchedule() {
		dbHelper.clearSchedule();
	}
	
	/*
	 * Displays a schedule event.
	 */
	private void displayEvent(ScheduleEvent event) {
				
		int height = event.getDuration();
		int topMargin = event.getStartMinutes() - (current_first_hour * 60);

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
	 * Hides hours more than one hour before/after the first/last event in the
	 * schedule.
	 */
	private void setVisibleHours() {
		
		current_first_hour = dbHelper.getEarliestHour() - 1;
		current_last_hour = dbHelper.getLatestHour() + 1;
				
		// Restrict to available hours
		if (current_first_hour < FIRST_HOUR) {
			current_first_hour = FIRST_HOUR;
		}
		if (current_last_hour > LAST_HOUR) {
			current_last_hour = LAST_HOUR;
		}
		
		// Show at least 8 hours (enough to fill the screen)
		if ((current_last_hour - current_first_hour) < 8) {
			current_last_hour = current_first_hour + 8;
		}
				
		// Show/remove appropriate hours
		for (int i = FIRST_HOUR; i <= LAST_HOUR; i++) {
			
			// If hour between first/last hours, show
			if (i > current_first_hour && i < current_last_hour) {
			
				TextView textView = (TextView) view.findViewWithTag("time" + i);
				textView.setVisibility(View.VISIBLE);
			
			// else hide hour
			} else {
				
				TextView textView = (TextView) view.findViewWithTag("time" + i);
				textView.setVisibility(View.GONE);
				
			}
			
		}
		
		// Set height of schedule layout
		int height = 60 * (current_last_hour - current_first_hour - 1);
		height = dpToPixels(height);
		
		LinearLayout scheduleLayout = (LinearLayout) view.findViewById(R.id.schedule_main);
		scheduleLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
		
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

