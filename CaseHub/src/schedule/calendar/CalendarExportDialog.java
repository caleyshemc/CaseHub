package schedule.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import schedule.ScheduleDBHelper;
import schedule.ScheduleEvent;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.casehub.R;

public class CalendarExportDialog extends DialogFragment {
	
	private View view;
	private DatePicker startDatePicker;
	private DatePicker endDatePicker;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate dialog_autosilent view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_calendar_export, null);
        builder.setView(view);
        
        // Grab datepickers
        startDatePicker = (DatePicker) view.findViewById(R.id.dpStartDate);
        endDatePicker = (DatePicker) view.findViewById(R.id.dpEndDate);
        		
        builder.setTitle(R.string.calendar_export_title)
				.setPositiveButton(R.string.export,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								// Get start and end dates
								Calendar startDate = Calendar.getInstance();
								startDate.set(
										startDatePicker.getYear(), 
										startDatePicker.getMonth(), 
										startDatePicker.getDayOfMonth());
								
								Calendar endDate = Calendar.getInstance();
								endDate.set(
										endDatePicker.getYear(), 
										endDatePicker.getMonth(), 
										endDatePicker.getDayOfMonth());
								
								
								// Get events
								ScheduleDBHelper dbHelper = new ScheduleDBHelper();
								ArrayList<ScheduleEvent> events = dbHelper.getSchedule();
								
								// Export to calendar
								CalendarHelper calHelper = new CalendarHelper(getActivity());
								calHelper.exportEvents(events, startDate, endDate);
							
								// Dismiss dialog
								CalendarExportDialog.this.getDialog().dismiss();
								
							}
						})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                // User cancelled the dialog
		            	CalendarExportDialog.this.getDialog().cancel();
		            }
		        });;
	 
		 // Create the AlertDialog object and return it
		 return builder.create();

		
	}

}
