package schedule;

import java.util.concurrent.ExecutionException;

import com.casehub.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;

public class LoginDialogFragment extends DialogFragment {
	
	private View view;
	
	// Username and password fields
	private EditText userText;
	private EditText passText;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
				
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate dialog_login view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view);
        
        builder.setMessage(R.string.login_prompt)
               .setPositiveButton(R.string.login_button, new DialogInterface.OnClickListener() {
            	   @Override
                   public void onClick(DialogInterface dialog, int id) {

                	   // Grab username and password
            		   userText = (EditText) view.findViewById(R.id.username);
            		   passText = (EditText) view.findViewById(R.id.password);
            		   
            		   String user = userText.getText().toString();
            		   String pass = passText.getText().toString();
            		   
            		   // TODO save username!
            		   // check for '@case.edu' and remove
            		   
            		   /* Log in using Case Single-Sign On*/
            			String html = "";
            			try {
            				html = new CaseSSOConnector().execute(user, pass).get();
            			} catch (InterruptedException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (ExecutionException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
            			
            			Log.d("RESPONSE TEXT", html);
            			
            			// TODO pass html to ScheduleFragment and close dialog

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

