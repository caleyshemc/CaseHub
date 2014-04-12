package schedule.autosilent;

import schedule.ScheduleFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.casehub.R;

public class AutoSilentDialog extends DialogFragment {
	
	private View view;
	
	private static SilenceReceiver silenceReceiver;
	private static UnsilenceReceiver unsilenceReceiver;
	
	private int newSetting;

	public AutoSilentDialog() {
		
		// Keep receivers static
		if (silenceReceiver == null) {
			silenceReceiver = new SilenceReceiver();
		}
		
		if (unsilenceReceiver == null) {
			unsilenceReceiver = new UnsilenceReceiver();
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate dialog_autosilent view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_autosilent, null);
        builder.setView(view);
        
        // Get current autosilent setting
        SharedPreferences settings = getActivity().getSharedPreferences(ScheduleFragment.SILENT_PREF, 0);
		int autoSilentSetting = settings.getInt(ScheduleFragment.SILENT, ScheduleFragment.SILENT_OFF);
		
        builder.setTitle(R.string.autosilent_title)
		        .setSingleChoiceItems(R.array.autosilent_options, autoSilentSetting, 
		                new DialogInterface.OnClickListener() {
		             
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	// Set new setting to selected option
		            	newSetting = which;
		            }
		            
		        })
				.setPositiveButton(R.string.okay,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								// Save preference
								SharedPreferences settings = getActivity().getSharedPreferences(
		            					ScheduleFragment.SILENT_PREF, 0);
		            		    SharedPreferences.Editor editor = settings.edit();
		            		    editor.putInt(ScheduleFragment.SILENT, newSetting);
		            		    editor.commit();
		            		    
		            		    // Create or cancel alarms for silent/unsilent events
		            		    // and reset ActionBar icon
		            		    if (newSetting == ScheduleFragment.SILENT_OFF) {
		            		    	
									silenceReceiver.cancel(getActivity());
									unsilenceReceiver.cancel();
									
								} else {
									
									silenceReceiver.schedule(getActivity());
									unsilenceReceiver.schedule(getActivity());
									
								}
		            		    
		            		    // TODO change icon
		            		    
							}
						})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                // User cancelled the dialog
		            	AutoSilentDialog.this.getDialog().cancel();
		            }
		        });;
	 
		 // Create the AlertDialog object and return it
		 return builder.create();
        
	}
	
}
