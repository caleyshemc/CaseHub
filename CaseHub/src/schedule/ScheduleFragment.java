package schedule;

import java.util.ArrayList;

import schedule.autosilent.AutoSilentDialog;
import schedule.calendar.CalendarExportDialog;
import schedule.login.LoginDialog;
import schedule.login.LoginTask;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.casehub.R;

public class ScheduleFragment extends Fragment {
	
	ScheduleDBHelper dbHelper;
	ScheduleUIHelper uiHelper;
	View view;
	MenuItem silentButton;
	
	/**
	 * Activate Scheduler test.
	 * 
	 * true = Scheduler test (inserts hard-coded values into database) 
	 * false = Original functioning (logs in to Case Single Sign-On and gets 
	 * 		   schedule from Scheduler).
	 * 
	 * This was added for testing/demo purposes after we found out, two days
	 * before the final code was due, that Scheduler has been retired. More info
	 * in final project report.
	 * 
	 */
	public static final boolean SCHEDULER_TEST = true;
	
	/**
	 * ActionBar item IDs
	 */
	public static final int REFRESH_ID = 0;
	public static final int SILENT_ID = 1;
	public static final int CAL_ID = 2;
	
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
		
	/**
	 * For returning LoginTask
	 */
	public interface LoginCallback {
        public void onTaskDone(ArrayList<ScheduleEvent> events);
    }
	
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
		uiHelper = new ScheduleUIHelper(getActivity(), view);
				
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
			uiHelper.displayEvents(events, dbHelper, getFragmentManager());
			
		}
		
		uiHelper.placeTimeLine();
		
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
	    silentButton = menu.add(0, SILENT_ID, 20, R.string.schedule_silent);
	    
	    if (autoSilentSetting == SILENT_OFF) {
	    	silentButton.setIcon(R.drawable.ic_action_volume_on).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    } else {
	    	silentButton.setIcon(R.drawable.ic_action_volume_muted).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    }
	    
	    // Show calendar export button
	    MenuItem calButton = menu.add(0, CAL_ID, 30, R.string.calendar_export);
	    calButton.setIcon(R.drawable.ic_action_go_to_today).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

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
	        	silentDialog.show(getFragmentManager(), "autosilent");
	        	return true;
	        	
	        case CAL_ID:
	        	DialogFragment calDialog = new CalendarExportDialog();
	        	calDialog.show(getFragmentManager(), "cal_export");
	        	return true;
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	public void login(String user, String pass) {
				
		new LoginTask(getActivity(), new LoginCallback() {
			
			@Override
			public void onTaskDone(ArrayList<ScheduleEvent> events) {
				
				/* Added for Scheduler test */
				if (SCHEDULER_TEST) {
					
					// Create hard-coded list of events
					ArrayList<ScheduleEvent> test_events = new ArrayList<ScheduleEvent>();
					
					test_events.add(new ScheduleEvent(1, "COGS 101", "Nord 400", "1230pm", "0145pm", Day.TUES));
					test_events.add(new ScheduleEvent(1, "COGS 101", "Nord 400", "1230pm", "0145pm", Day.THURS));
					
					test_events.add(new ScheduleEvent(2, "ENGR 131", "Yost 101", "1000am", "1115am", Day.MON));
					test_events.add(new ScheduleEvent(2, "ENGR 131", "Yost 101", "1000am", "1115am", Day.WED));
					test_events.add(new ScheduleEvent(2, "ENGR 131", "Yost 101", "1000am", "1115am", Day.FRI));
					
					test_events.add(new ScheduleEvent(3, "MATH 402", "Crawford 600", "0230pm", "0345pm", Day.TUES));
					test_events.add(new ScheduleEvent(3, "MATH 402", "Crawford 600", "0230pm", "0345pm", Day.THURS));
					
					test_events.add(new ScheduleEvent(4, "ARTS 300", "Sears 100", "1130am", "1220pm", Day.MON));
					test_events.add(new ScheduleEvent(4, "ARTS 300", "Sears 100", "1130am", "1220pm", Day.WED));
					test_events.add(new ScheduleEvent(4, "ARTS 300", "Sears 100", "1130am", "1220pm", Day.FRI));
					
					onLoginComplete(test_events);
					
				} else {
					onLoginComplete(events);
				}
				
			}
		}).execute(user, pass);		
		
	}
	
	private void onLoginComplete(ArrayList<ScheduleEvent> events) {
		
		// If successful, set preference indicating user has logged in
		SharedPreferences settings = getActivity().getSharedPreferences(
				ScheduleFragment.LOGGED_IN_PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(ScheduleFragment.LOGGED_IN, true);
		editor.commit();
		
		clearSchedule();
		addEvents(events);

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
		uiHelper.displayEvents(events, dbHelper, getFragmentManager());
		
	}
	
	/**
	 * Deletes all schedule information from the event table
	 * and layout
	 */
	public void clearSchedule() {
		dbHelper.clearSchedule();
		
		// Remove events from layout
		RelativeLayout layout;
		
		for (Day day : Day.values()) {
			layout = (RelativeLayout) view.findViewWithTag(day.getString());
			layout.removeViewsInLayout(1, layout.getChildCount() - 1);
		}
	}
	
	/**
	 * Changes autosilent button icon.
	 */
	public void setSilentButton(int setting) {
		
		if (setting == SILENT_OFF) {
			silentButton.setIcon(R.drawable.ic_action_volume_on);
		} else {
			silentButton.setIcon(R.drawable.ic_action_volume_muted);
		}
		
	}
	
}

