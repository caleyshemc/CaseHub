package schedule;

import java.util.concurrent.ExecutionException;

import com.casehub.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScheduleFragment extends Fragment {

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
		
		super.onViewCreated(view, savedInstanceState);
		
	}


}

