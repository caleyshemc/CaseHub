package schedule;

import java.security.InvalidParameterException;

import org.joda.time.LocalTime;

import com.casehub.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
		Day[] days = new Day[] {Day.MON};
		ScheduleEvent event = new ScheduleEvent("EECS 395", "Olin 314", LocalTime.now(), LocalTime.now(), days);
		addEvent(event);
		
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	/**
	 * Add an event to the schedule.
	 */
	private void addEvent(ScheduleEvent event) {
		
		// Grab event layout template
		LayoutInflater inflater =
			    (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		LinearLayout layout = (LinearLayout) inflater.inflate( R.layout.template_event_layout, null );
		
		TextView name = (TextView) layout.findViewWithTag("name");
		TextView time = (TextView) layout.findViewWithTag("time");
		TextView location = (TextView) layout.findViewWithTag("location");
				
		// Set event text values
		name.setText(event.getName());
		time.setText(event.getTimeString());
		location.setText(event.getLocation());
		
		// Set event layout properties
		int height = event.getDuration();
		int topMargin = event.getStartMinutes() - (FIRST_HOUR * 60);
		
		if (height < 1) {
			throw new InvalidParameterException("Error: Event duration must be at least 1 minute.");
		}
		if (topMargin < 0) {
			throw new InvalidParameterException("Error: Events cannot start before " + FIRST_HOUR);
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
		params.setMargins(0, topMargin, 0, 0);
		
		// TODO set layout_height and layout_marginTop according to time
		
		// For each day of the week this event occurs, add to layout
		// TODO currently fails when adding more than one.
		// Perhaps I have to inflate the template twice, or duplicate it?
		int layoutId;
		RelativeLayout parentLayout;
		
		for (Day day : event.getDays()) {
			
			// Get parent layout
			layoutId = getResources().getIdentifier(
					day.getString(),
				    "id",
				    this.getActivity().getPackageName());
			parentLayout = (RelativeLayout) getView().findViewById(layoutId);
			
			// Add new event layout to parent layout
			parentLayout.addView(layout);
			
		}
		

	}

}

