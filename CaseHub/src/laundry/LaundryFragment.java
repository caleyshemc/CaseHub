package laundry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
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
	
	/**
	 * SharedPreferences fields and filenames
	 */
	public static final String LAUNDRY_PREFS = "LaundryPrefsFile";
	public static final String HOUSES_LOADED = "housesLoaded";
	
	/**
	 * For returning FetchLaundryTask
	 */
	public interface LaundryCallback {
        public void onTaskDone(ArrayList<LaundryMachine> machines);
    }
	
	/**
	 * For returning FetchHousesTask
	 */
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
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
				
		getActivity().getMenuInflater().inflate(R.menu.laundry, menu);
		
		// Grab spinner from ActionBar (for adding houses)
		MenuItem item = menu.findItem(R.id.laundry_spinner);
	    spinner = (Spinner) MenuItemCompat.getActionView(item);
	    
		// Check if houses have been loaded previously
		SharedPreferences settings = getActivity().getSharedPreferences(
				LAUNDRY_PREFS, 0);
		boolean housesLoaded = settings.getBoolean(HOUSES_LOADED, false);

		if (housesLoaded) {

			// Show houses in database
			HashMap<String, Integer> houses = dbHelper.getHouses();
			Log.d("LAUNDRY", "Houses found in DB: " + houses.toString());
			populateHouseSpinner(houses);

			// TODO go to last-opened house

		} else {

			// Fetch houses from eSuds
			new FetchHousesTask(getActivity(), new LaundryHousesCallback() {

				@Override
				public void onTaskDone(HashMap<String, Integer> houses) {
					onHousesFetched(houses);
				}
			}).execute();

		}
	    
	}
	
	/**
	 * Called when ActionBar button is selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.refresh_houses:
			dbHelper.clearHouses();
			new FetchHousesTask(getActivity(), new LaundryHousesCallback() {
				
				@Override
				public void onTaskDone(HashMap<String, Integer> houses) {
					onHousesFetched(houses);
				}
			}).execute();
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Called when houses are successfully fetched from eSuds.
	 */
	private void onHousesFetched(HashMap<String, Integer> houses) {
		
		// Set preference indicating houses have been fetched
		SharedPreferences settings = getActivity().getSharedPreferences(
				LAUNDRY_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(HOUSES_LOADED, true);
		editor.commit();
		
		dbHelper.addHouses(houses);
		populateHouseSpinner(houses);
		
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
				R.layout.spinner_item_laundry, houseArray);
		adapter.setDropDownViewResource(R.layout.spinner_item_laundry);
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, 
		            int pos, long id) {

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

