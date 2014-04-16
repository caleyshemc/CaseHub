package laundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import schedule.ScheduleEvent;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.casehub.R;

public class LaundryFragment extends Fragment {
	
	View view;
	Spinner spinner;
	
	HashMap<String, Integer> houses;
	
	public interface LaundryCallback {
        public void onTaskDone(ArrayList<LaundryMachine> machines);
    }
	
	public interface LaundryHousesCallback {
		public void onTaskDone(HashMap<String, Integer> houses);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	view = inflater.inflate(R.layout.fragment_laundry, container, false);
		return view;
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
	
		spinner = (Spinner) view.findViewById(R.id.house_spinner);
		
		new FetchHousesTask(getActivity(), new LaundryHousesCallback() {
			
			@Override
			public void onTaskDone(HashMap<String, Integer> houses) {
				populateHouseSpinner(houses);
			}
		}).execute();
		
	}
	
	private void populateHouseSpinner(HashMap<String, Integer> houses) {
		
		this.houses = houses;
		
		ArrayList<String> houseList = new ArrayList<String>(houses.keySet());
		String[] houseArray = houseList.toArray(new String[houseList.size()]);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, houseArray);
		spinner.setAdapter(adapter);
		
	}
    
}

