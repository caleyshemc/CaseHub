package schedule;

import java.util.concurrent.ExecutionException;

import com.casehub.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class LoginDialogFragment extends DialogFragment {
	
	private View view;
	
	// Username and password fields
	private EditText userText;
	private EditText passText;
	
	ProgressBar progressBar;
	
	OnLoginListener callback;

	/**
	 * Define callback interface for communicating with MainActivity.
	 * Container Activity must implement this interface.
	 */
    public interface OnLoginListener {
        public void onScheduleLogin(String html);
    }
    
    /**
     * Makes sure that the container activity has implemented
     * the callback interface. If not, throws an exception.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
            callback = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginListener");
        }
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
				
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate dialog_login view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view);
        
        // Find layout elements
        userText = (EditText) view.findViewById(R.id.username);
		passText = (EditText) view.findViewById(R.id.password);
		progressBar = (ProgressBar) view.findViewById(R.id.login_progress);
        
        builder.setMessage(R.string.login_prompt)
               .setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
            	   @Override
                   public void onClick(DialogInterface dialog, int id) {
            		   
                	   // Grab username and password
            		   String user = userText.getText().toString();
            		   String pass = passText.getText().toString();
            		   
            		   // Hide fields, show progress bar
            		   userText.setVisibility(2);
            		   passText.setVisibility(2);
            		   progressBar.setVisibility(0);
            		   
            		   /* Log in using Case Single-Sign On*/
            			String html = "";
            			try {
            				html = new LoginTask().execute(user, pass).get();
            			} catch (InterruptedException e) {
            				// TODO handle
            				Log.e("CASEHUB", "exception", e);
            			} catch (ExecutionException e) {
            				Log.e("CASEHUB", "exception", e);
            			}
            			
            			// If successful, set preference indicating user has logged in
            			SharedPreferences settings = getActivity().getSharedPreferences(
            					ScheduleFragment.LOGIN_PREF, 0);
            		    SharedPreferences.Editor editor = settings.edit();
            		    editor.putBoolean(ScheduleFragment.LOGGED_IN, true);
            		    editor.commit();

            			// Pass HTML to MainActivity and dismiss dialog
            			callback.onScheduleLogin(html);
            			LoginDialogFragment.this.getDialog().dismiss();

                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   LoginDialogFragment.this.getDialog().cancel();
                   }
               });;
        
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

