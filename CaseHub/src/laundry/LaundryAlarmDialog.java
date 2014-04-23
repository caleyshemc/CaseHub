package laundry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.casehub.R;

public class LaundryAlarmDialog extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_laundry_alarm, null);
        
        // TODO check if available! if not, can't set alarm
        Bundle mArgs = getArguments();
        String type = mArgs.getString("type");
        int machineNumber = mArgs.getInt("machineNumber");
        int minutesLeft = mArgs.getInt("minutesLeft");
        
        builder.setView(view);
        builder.setTitle(R.string.laundry_alarm_title)
        		.setMessage("Set alarm for " + type + " " + machineNumber + "?")
        		.setPositiveButton(R.string.set_alarm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
		            		    
								/* TODO
								 * 
								 * Just grab time and set an alarm for that!
								
		            		    // Create or cancel alarms for silent/unsilent events		            		    
		            		    if (newSetting == ScheduleFragment.SILENT_OFF) {
									silenceReceiver.cancel(getActivity());
									unsilenceReceiver.cancel();
								} else {
									silenceReceiver.schedule(getActivity());
									unsilenceReceiver.schedule(getActivity());
								}
								*/
		            		    
							}
						})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                // User cancelled the dialog
		            	LaundryAlarmDialog.this.getDialog().cancel();
		            }
		        });;
	 
		 // Create the LaundryAlarmDialog object and return it
		 return builder.create();
        
	}
	
}
