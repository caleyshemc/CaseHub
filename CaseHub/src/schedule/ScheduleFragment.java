package schedule;

import java.security.InvalidParameterException;

import org.joda.time.LocalTime;

import com.casehub.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		// TODO Check if login is needed
		// https://login.case.edu/cas/login?service=http://scheduler.case.edu/
				
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }
	
	// TODO: this is just a test!
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		/* Log in using Case Single-Sign On
		
		String targetURL = "https%3a%2f%2fm.case.edu%2fgadget_s.html%3f_gid%3dmyschedule";
		String text = "Failed in onViewCreated()";
		try {
			text = new CaseSSOConnector().execute("","").get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView tv = (TextView) view.findViewById(R.id.test_text);  
		tv.setText(text);
		*/
		
		/* Testing event adding */
		Day[] days = new Day[] {Day.MON, Day.WED};
		ScheduleEvent event = new ScheduleEvent("EECS 395", "Olin 314", LocalTime.now(), LocalTime.now().plusHours(1), days);
		addEvent(event);
		
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
			
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeLine.getLayoutParams();
			params.topMargin = dpToPixels(minutes - (FIRST_HOUR*60));
			
		} else { 
			// Hide line
			// TODO
		}
		
	}
	
	/**
	 * Add an event to the schedule.
	 */
	public void addEvent(ScheduleEvent event) {
		int height = event.getDuration();
		int topMargin = event.getStartMinutes() - (FIRST_HOUR*60);
		
		if (height < 1) {
			throw new InvalidParameterException("Error: Event duration must be at least 1 minute.");
		}
		if (topMargin < 0) {
			throw new InvalidParameterException("Error: Events cannot start before hour " + FIRST_HOUR);
		}
		if ((height + topMargin) > LAST_HOUR*60) {
			throw new InvalidParameterException("Error: Events cannot end after hour " + LAST_HOUR);
		}
		
		// Grab event layout template
		LayoutInflater inflater =
			    (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		LinearLayout layout = (LinearLayout) inflater.inflate( R.layout.template_event_layout, null );
		
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
		
		for (Day day : event.getDays()) {
			
			// Clone layout by inflating template
			eventLayout = (LinearLayout) inflater.inflate( R.layout.template_event_layout, null );
			eventLayout.setLayoutParams(params);
			
			// Set event text values
			TextView name = (TextView) eventLayout.findViewWithTag("name");
			TextView time = (TextView) eventLayout.findViewWithTag("time");
			TextView location = (TextView) eventLayout.findViewWithTag("location");
					
			name.setText(event.getName());
			time.setText(event.getTimeString());
			location.setText(event.getLocation());
			
			// Get parent layout
			layoutId = getResources().getIdentifier(
					day.getString(),
				    "id",
				    this.getActivity().getPackageName());
			parentLayout = (RelativeLayout) getView().findViewById(layoutId);
			
			// Add new event layout to parent layout
			parentLayout.addView(eventLayout);
			
		}
		
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

