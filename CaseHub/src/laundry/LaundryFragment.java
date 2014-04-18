package laundry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.casehub.R;

public class LaundryFragment extends Fragment {
	
	Spinner spinner;
	HashMap<String, Integer> houses;
	
	public interface LaundryCallback {
        public void onTaskDone(ArrayList<LaundryMachine> machines);
    }
	
	public interface LaundryHousesCallback {
		public void onTaskDone(HashMap<String, Integer> houses);
	}
	public LaundryFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		
		new FetchHousesTask(getActivity(), new LaundryHousesCallback() {
			
			@Override
			public void onTaskDone(HashMap<String, Integer> houses) {
				populateHouseSpinner(houses);
			}
		}).execute();
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		getActivity().getMenuInflater().inflate(R.menu.laundry, menu);
		
		// Grab spinner from ActionBar (for adding houses)
		MenuItem item = menu.findItem(R.id.laundry_spinner);
	    spinner = (Spinner) MenuItemCompat.getActionView(item);
	    
	}
	
	private ArrayAdapter<String> populateHouseSpinner(HashMap<String, Integer> houses) {
		
		if (houses.isEmpty()) {
			throw new InvalidParameterException(
					"List of houses in LaundryFragment cannot be empty.");
		}
		
		this.houses = houses;
		
		// Create array of house names
		ArrayList<String> houseList = new ArrayList<String>(houses.keySet());
		Collections.sort(houseList, String.CASE_INSENSITIVE_ORDER);
		String[] houseArray = houseList.toArray(new String[houseList.size()]);
		
		// Populate spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, houseArray);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {

				Log.d("LAUNDRY", "Item selected!");
				String selectedHouse = (String) parent.getItemAtPosition(pos);
				onHouseSelected(selectedHouse);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});
	    
		return adapter;
	}
	
	private void onHouseSelected(String houseName) {
		
		int selectedHouseId = houses.get(houseName);
				
		new FetchLaundryTask(getActivity(), new LaundryCallback() {
			
			@Override
			public void onTaskDone(ArrayList<LaundryMachine> machines) {
				showLaundryTimes(machines);
			}
		}, selectedHouseId).execute();
		
	}
	
	private void showLaundryTimes(ArrayList<LaundryMachine> machines) {
		
		LinearLayout laundryLayout = (LinearLayout) getActivity().findViewById(R.id.laundry_main);
		
		// TODO populate layout with machines!
		
		
	}
    
}

