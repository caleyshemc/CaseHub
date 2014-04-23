package laundry.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.casehub.R;

public class LaundryAlarmDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		

        // Inflate view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_laundry_alarm, null);
        
        // TODO check if available! if not, can't set alarm
        Bundle args = getArguments();
        final int houseId = args.getInt("houseId");
        final String type = args.getString("type");
        final String status = args.getString("status");
        final int machineNumber = args.getInt("machineNumber");
        
        TextView text = (TextView) view.findViewById(R.id.laundry_dialog_text);
        text.setText("Set alarm for " + type + " " + machineNumber + "?");
        
        builder.setView(view);
        builder.setPositiveButton(R.string.set_alarm,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
		            		    
								// Set alarm for change in machine status
								new LaundryAlarmReceiver().setAlarm(getActivity(),
										houseId, machineNumber, status);

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
