package schedule;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalTime;

import com.casehub.R;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Performs UI tasks for ScheduleFragment
 */
public class ScheduleUIHelper {

	View view;
	Context context;
	
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
	
	public ScheduleUIHelper(Context context, View view) {
		this.context = context;
		this.view = view;
	}
	
	/**
	 * Places the line indicating the current time.
	 */
	public void placeTimeLine() {
		LocalTime now = LocalTime.now();
		int minutes = (now.getHourOfDay() * 60) + now.getMinuteOfHour();
		LinearLayout timeLine = (LinearLayout) view.findViewById(R.id.current_time);
		
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
	 * Displays events in the schedule.
	 */
	public void displayEvents(ArrayList<ScheduleEvent> events, ScheduleDBHelper dbHelper) {

		HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
		
		int[] colorArray = {
			context.getResources().getColor(R.color.event1),
			context.getResources().getColor(R.color.event2),
			context.getResources().getColor(R.color.event3),
			context.getResources().getColor(R.color.event4),
			context.getResources().getColor(R.color.event5),
			context.getResources().getColor(R.color.event6)
		};
		
		int earlistHour = dbHelper.getEarliestHour();
		int latestHour = dbHelper.getLatestHour();
		setVisibleHours(earlistHour, latestHour);

		int eventId;
		int colorIndex = 0;
		
		// Set event color and display
		for (ScheduleEvent event : events) {
			
			eventId = event.getId();
			
			if (!colorMap.containsKey(eventId)) {
				colorMap.put(eventId, colorArray[colorIndex]);
				colorIndex = (colorIndex + 1) % colorArray.length;
			}
			
			displayEvent(event, colorMap.get(eventId));
			
		}
		
	}
	
	/*
	 * Displays a schedule event.
	 */
	private void displayEvent(ScheduleEvent event, int color) {
				
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
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.template_event_layout, null);

		// Set layout dimensions
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.height = dpToPixels(height - 1);
		params.setMargins(0, dpToPixels(topMargin), 0, 0);
		layout.setLayoutParams(params);

		// Clone layout by inflating template
		LinearLayout eventLayout = (LinearLayout) inflater.inflate(R.layout.template_event_layout, null);
		eventLayout.setLayoutParams(params);
		
		// Set color
		eventLayout.setBackgroundColor(color);

		// Set event text values
		TextView name = (TextView) eventLayout.findViewWithTag("name");
		TextView time = (TextView) eventLayout.findViewWithTag("time");
		TextView location = (TextView) eventLayout.findViewWithTag("location");

		name.setText(event.getName());
		time.setText(event.getTimeString());
		location.setText(event.getLocation());

		// Get parent layout
		String day = event.getDay().getString();
		int layoutId = context.getResources().getIdentifier(day, "id", context.getPackageName());
		RelativeLayout parentLayout = (RelativeLayout) view.findViewById(layoutId);

		// Add new event layout to parent layout
		parentLayout.addView(eventLayout);
	}
	
	/*
	 * Hides hours more than one hour before/after the first/last event in the
	 * schedule.
	 */
	private void setVisibleHours(int earliestHour, int latestHour) {
		
		current_first_hour = earliestHour - 1;
		current_last_hour = latestHour + 1;
				
		// Restrict to available hours
		if (current_first_hour < FIRST_HOUR) {
			current_first_hour = FIRST_HOUR;
		}
		if (current_last_hour > LAST_HOUR) {
			current_last_hour = LAST_HOUR;
		}
		
		// Show at least 8 hours (enough to fill most screens)
		if ((current_last_hour - current_first_hour) < 8) {
			current_last_hour = current_first_hour + 8;
		}
				
		// Remove hours before current_first_hour
		for (int i = FIRST_HOUR; i < current_first_hour; i++) {

			TextView textView = (TextView) view.findViewWithTag("time" + i);
			textView.setVisibility(View.GONE);
			
		}
		
		// Set height of schedule layout
		int height = 60 * (current_last_hour - current_first_hour + 1);
		height = dpToPixels(height);
		
		LinearLayout scheduleLayout = (LinearLayout) view.findViewById(R.id.schedule_main);
		scheduleLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
		
	}
	
	/*
	 * Converts dp to pixels, as dp cannot be set directly at runtime.
	 * Used for setting layout parameters.
	 */
	private int dpToPixels(int dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		float fpixels = metrics.density * dp;
		int pixels = (int) (fpixels + 0.5f);
		
		return pixels;

	}
	
}
