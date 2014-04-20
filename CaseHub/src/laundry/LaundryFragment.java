package laundry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.casehub.R;

public class LaundryFragment extends Fragment {
	
	LaundryDbHelper dbHelper;
	Spinner spinner;
	HashMap<String, Integer> houses;
	
	public interface LaundryCallback {
        public void onTaskDone(ArrayList<LaundryMachine> machines);
    }
	
	public interface LaundryHousesCallback {
		public void onTaskDone(HashMap<String, Integer> houses);
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
	 * -- Open to last-viewed house 
	 * -- Query for houses only on first open or when "Refresh House List" menu button clicked
	 * 	  -- save houses in db!
	 */
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		dbHelper = new LaundryDbHelper();
		
		new FetchHousesTask(getActivity(), new LaundryHousesCallback() {
			
			@Override
			public void onTaskDone(HashMap<String, Integer> houses) {
				dbHelper.addHouses(houses);
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
		adapter.setDropDownViewResource(R.layout.spinner_item_laundry);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {

				((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
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
		
		// TODO null check, empty check
		
		LinearLayout laundryLayout = (LinearLayout) getActivity().findViewById(R.id.laundry_main);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		// Remove current children of layout
		laundryLayout.removeAllViews();
		
		// Populate with laundry info
		for (LaundryMachine machine : machines) {
			
            Button myButton = new Button(getActivity());
            myButton.setText(machine.toString());
            
            laundryLayout.addView(myButton, layoutParams);
			
		}
		
	}
    
}

