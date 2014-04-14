package schedule.calendar;

import schedule.ScheduleFragment;
import schedule.autosilent.AutoSilentDialog;

import com.casehub.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class CalendarExportDialog extends DialogFragment {
	
	private View view;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate dialog_autosilent view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_calendar_export, null);
        builder.setView(view);
        		
        builder.setTitle(R.string.calendar_export_title)
				.setPositiveButton(R.string.export,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								// TODO do stuff
								
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
