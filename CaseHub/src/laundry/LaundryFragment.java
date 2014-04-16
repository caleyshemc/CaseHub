package laundry;

import java.util.ArrayList;

import schedule.ScheduleEvent;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.casehub.R;

public class LaundryFragment extends Fragment {
	
	public interface LaundryCallback {
        public void onTaskDone();
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laundry, container, false);
    }
    
    /*
	 * TODO 
	 * -- Query for list of houses when first opened 
	 * -- Open to last-viewed house 
	 * -- Query for specific house only when refresh button hit or new
	 * house selected in drop-down
	 */
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		new FetchLaundryTask(getActivity(), new LaundryCallback() {
			
			@Override
			public void onTaskDone() {
				// TODO Auto-generated method stub
				Log.d("LAUNDRY", "It works!");
			}
		}).execute();
		
		/*
		String esudsHTML = "";
		
		try {
			esudsHTML = new FetchLaundryTask().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d("LAUNDRY", esudsHTML);
		*/
	}
    
}

