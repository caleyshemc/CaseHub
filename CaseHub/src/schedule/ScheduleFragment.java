package schedule;

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
	
	@Override
	public void onViewCreated (View view, Bundle savedInstanceState) {
		
		TextView tv = (TextView) view.findViewById(R.id.test_text);  
		tv.setText("Text to set");
		
		super.onViewCreated(view, savedInstanceState);
		
	}


}

