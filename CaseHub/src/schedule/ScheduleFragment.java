package schedule;

import com.example.casehub.R;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;

// TODO: rename to ScheduleLoginFragment and create separate SceduleFragment? Or make an Activity?
public class ScheduleFragment extends DialogFragment {

	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.login_prompt)
               .setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
               });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

