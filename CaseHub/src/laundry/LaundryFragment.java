package laundry;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	public static final String HOUSES_LOADED_PREFS = "housesLoadedPrefsFile";
	public static final String HOUSES_LOADED = "housesLoaded";
	public static final String CURRENT_HOUSE_PREFS = "currentHousePrefsFile";
	public static final String CURRENT_HOUSE = "currentHouse";
	
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
    
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		dbHelper = new LaundryDbHelper();
		
	}
	
	/**
	 * TODO:
	 * -- Fetches laundry times when drawer opened! :O
	 */
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
				
		getActivity().getMenuInflater().inflate(R.menu.laundry, menu);
		
		// Grab spinner from ActionBar (for adding houses)
		MenuItem item = menu.findItem(R.id.laundry_spinner);
	    spinner = (Spinner) MenuItemCompat.getActionView(item);
	    
		// Check if houses have been loaded previously
		SharedPreferences settings = getActivity().getSharedPreferences(
				HOUSES_LOADED_PREFS, 0);
		boolean housesLoaded = settings.getBoolean(HOUSES_LOADED, false);

		if (housesLoaded) {

			// Show houses in database
			HashMap<String, Integer> houses = dbHelper.getHouses();
			populateHouseSpinner(houses);

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
				HOUSES_LOADED_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(HOUSES_LOADED, true);
		editor.commit();
		
		dbHelper.addHouses(houses);
		populateHouseSpinner(houses);
		
	}
	
	/*
	 * Populates spinner (select box in ActionBar) with given houses.
	 */
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
		
		// Populate spinner adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item_laundry, houseArray);
		adapter.setDropDownViewResource(R.layout.spinner_item_laundry);
	    spinner.setAdapter(adapter);
	    
		// Get last-opened house
		SharedPreferences houseSetting = getActivity().getSharedPreferences(
				CURRENT_HOUSE_PREFS, 0);
		String currentHouse = houseSetting.getString(CURRENT_HOUSE, "");

		// Set spinner to last-opened house
		if (currentHouse.length() > 0) {
			int spinnerPosition = adapter.getPosition(currentHouse);
			spinner.setSelection(spinnerPosition);
		}
	    
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
	
	/*
	 * Called when a house is selected in the spinner.
	 */
	private void onHouseSelected(String houseName) {
		
		int selectedHouseId = houses.get(houseName);
				
		new FetchLaundryTask(getActivity(), new LaundryCallback() {
			
			@Override
			public void onTaskDone(ArrayList<LaundryMachine> machines) {
				showLaundryTimes(machines);
			}
		}, selectedHouseId).execute();
				
		// Set preference indicating last house shown
		SharedPreferences settings = getActivity().getSharedPreferences(
				CURRENT_HOUSE_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(CURRENT_HOUSE, houseName);
		editor.commit();
		
	}
	
	private void showLaundryTimes(ArrayList<LaundryMachine> machines) {
		
		// TODO null check, empty check
		
		LinearLayout laundryLayout = (LinearLayout) getActivity().findViewById(R.id.laundry_main);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		// Remove current children of layout
		laundryLayout.removeAllViews();
		
		// Populate with laundry info
		for (LaundryMachine machine : machines) {
			
			final String type = machine.getType();
			final int machineNumber = machine.getMachineNumber();
			final int minutesLeft = machine.getMinutesLeft();
			
			RelativeLayout button = createMachineButton(machine);
            button.setOnClickListener(new View.OnClickListener() {
            	
                @Override
                public void onClick(View v) {
                	
                	// TODO check status here (or just minutes left!)
                	
                	// Create bundle of arguments to send to dialog
                	Bundle args = new Bundle();
                	args.putString("type", type);
                	args.putInt("machineNumber", machineNumber);
                	args.putInt("minutesLeft", minutesLeft);
                	
                	// Show dialog
                	DialogFragment alarmDialog = new LaundryAlarmDialog();
                	alarmDialog.setArguments(args);
                	alarmDialog.show(getFragmentManager(), "laundry_alarm");
                }
            });
            
            // Add button to layout
            laundryLayout.addView(button, layoutParams);
            
            // Add spacer between buttons
            View spacer = new View(getActivity());
            LayoutParams spacerParams = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
            int color = getActivity().getResources().getColor(R.color.main_bg);
            spacer.setBackgroundColor(color);
            
            laundryLayout.addView(spacer, spacerParams);
			
		}
		
		
	}
	
	private RelativeLayout createMachineButton(LaundryMachine machine) {
		
		String status = machine.getStatus().getString();
		String type = machine.getType();
		int minutesLeft = machine.getMinutesLeft();

		// Inflate button template
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout button = (RelativeLayout) inflater.inflate(
				R.layout.template_laundry_button, null);
		
		// Set washer/dryer icon
		Drawable icon;
		if (machine.getType().equals(LaundryMachine.TYPE_WASHER)) {
			icon = getActivity().getResources().getDrawable(R.drawable.washer_icon_small);
		} else {
			icon = getActivity().getResources().getDrawable(R.drawable.dryer_icon_small);
		}

		
		ImageView image = (ImageView) button.findViewById(R.id.laundry_icon);
		image.setImageDrawable(icon);
		
		// Set text
		TextView machineName = (TextView) button.findViewById(R.id.laundry_machine_name);
		machineName.setText(type + " " + machine.getMachineNumber());
		TextView machineStatus = (TextView) button.findViewById(R.id.laundry_machine_status);

		if (minutesLeft > 0) {
			machineStatus.setText(status + " (" + minutesLeft + " min left)");
		} else {
			machineStatus.setText(status);
		}

		// Set status color
		int color;
		if (machine.getStatus() == LaundryMachine.Status.AVAILABLE) {
			color = getActivity().getResources().getColor(R.color.laundry_available);
		} else {
			color = getActivity().getResources().getColor(R.color.laundry_unavailable);
		}

		machineStatus.setTextColor(color);
				
		return button;
	}
    
}

