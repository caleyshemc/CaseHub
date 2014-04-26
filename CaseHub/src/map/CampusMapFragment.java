package map;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import map.CaseMap.Point;

import casehub.MainActivity;

import com.casehub.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CampusMapFragment extends Fragment implements OnMarkerClickListener, OnEditorActionListener {

	private GoogleMap mMap;
	private CaseMap cMap = new CaseMap();
	private static View mView;
	static final CameraPosition CASE =
			new CameraPosition.Builder().target(new LatLng(41.5052695, -81.6082641))
			.zoom(17)
			.build();
	private EditText editText;
	public static final String DB_STATE_PREF = "DatabaseStateFile";
	public static final String DB_STATE = "DatabaseState=";
	public static final int DB_FULL = 1;
	public static final int DB_EMPTY = 0;
	private boolean[] states = {true, true, true, true, true, true, true, true, true};
	private String incoming = null;
	SparseArray<ArrayList<Point>> types = new SparseArray<ArrayList<Point>>();

	public void setIncoming(String incoming) {
		this.incoming = incoming;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null)
				parent.removeView(mView);
		}
		try {
			mView = inflater.inflate(R.layout.fragment_campusmap, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		} finally {
			setHasOptionsMenu(true);
			SharedPreferences settings = getActivity().getSharedPreferences(DB_STATE_PREF, 0);
			//If the database is empty, fill it and the databean and mark it filled, otherwise fill the databean from the database
			if(settings.getInt(DB_STATE, 0) == 0){
				parseJSON();
				writeDataBean();
				settings.edit().putInt(DB_STATE, DB_FULL).commit();
			}else if(cMap.numPoints() == 0){
				buildDataBean();
			}
			setUpMapIfNeeded();
			if(incoming != null){
				Marker point = cMap.getPoint(incoming).getMarker();
				onMarkerClick(point);
				incoming = null;
			}
		}
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.campusmap, menu);
		editText = (EditText) menu.findItem(R.id.action_search).getActionView();
		editText.setOnEditorActionListener(this);
		MenuItem menuItem = menu.findItem(R.id.action_search);
		menuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true; // Return true to collapse action view
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				editText.clearFocus();
				return true; // Return true to expand action view
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = (String) item.getTitle();
		if(item.getItemId() == R.id.action_types){
			final String[] items = getResources().getStringArray(R.array.maptypearray);
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.c);
			builder.setTitle(R.string.displayaction);
			builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener(){
				public void onClick(DialogInterface dialogInterface, int item, boolean state) {
				}
			});
			builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					int typeId = 0;
					for(boolean show : states){
						if(show){
							showMarkers(types.get(typeId));
						}else{
							hideMarkers(types.get(typeId));
						}
						typeId++;
					}
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			builder.create().show();
		}
		return false;
	}

	protected void hideMarkers(ArrayList<Point> arrayList) {
		for(Point curPoint : arrayList){
			curPoint.getMarker().setVisible(false);
		}
	}

	protected void showMarkers(ArrayList<Point> arrayList) {
		for(Point curPoint : arrayList){
			curPoint.getMarker().setVisible(true);
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.casemap)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMarkerClickListener(this);
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CASE));
				addMarkers();
			}
		}
	}

	public void parseJSON(){
		try {
			cMap = new ParseMapJSONTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void writeDataBean(){
		AsyncTask<CaseMap, Void, Void> task = new MapDBWriteTask();
		task.execute(cMap);
	}

	public void buildDataBean(){
		try {
			cMap = new MapDBReadTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void addMarkers(){
		ArrayList<Point> academic = new ArrayList<Point>();
		ArrayList<Point> administrative = new ArrayList<Point>();
		ArrayList<Point> residential = new ArrayList<Point>();
		ArrayList<Point> commons = new ArrayList<Point>();
		ArrayList<Point> restaurants = new ArrayList<Point>();
		ArrayList<Point> other = new ArrayList<Point>();
		ArrayList<Point> parkinglots = new ArrayList<Point>();
		ArrayList<Point> hospital = new ArrayList<Point>();
		ArrayList<Point> athletic = new ArrayList<Point>();
		for(Point curPoint : cMap.getPoints().values()){
			float color = 0f;
			switch(curPoint.getTypeId()){
			case 0:
				color = 120; //Green
				academic.add(curPoint);
				break;
			case 1:
				color = 240; //Blue
				administrative.add(curPoint);
				break;
			case 2:
				color = 0; //Red
				residential.add(curPoint);
				break;
			case 3:
				color = 30; //Orange
				commons.add(curPoint);
				break;
			case 4:
				color = 270; //Violet
				restaurants.add(curPoint);
				break;
			case 5:
				color = 300; //Magenta
				other.add(curPoint);
				break;
			case 6:
				color = 180; //Cyan
				parkinglots.add(curPoint);
				break;
			case 7:
				color = 70; //Yellow
				hospital.add(curPoint);
				break;
			case 8:
				color = 210; //Azure
				athletic.add(curPoint);
				break;
			}	
			Marker point = mMap.addMarker(new MarkerOptions()
			.position(curPoint.getCoord())
			.title(curPoint.getName())
			.icon(BitmapDescriptorFactory.defaultMarker(color)));
			curPoint.setMarker(point);
		}
		types.append(0, academic);
		types.append(1, administrative);
		types.append(2, residential);
		types.append(3, commons);
		types.append(4, restaurants);
		types.append(5, other);
		types.append(6, parkinglots);
		types.append(7, hospital);
		types.append(8, athletic);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17);
		mMap.animateCamera(cameraUpdate);
		marker.showInfoWindow();
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_SEARCH){
			FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
			SearchMapFragment fragment = new SearchMapFragment();
			Bundle args = new Bundle();
			args.putString("query", editText.getText().toString());
			fragment.setArguments(args);
			ft.replace(R.id.content_frame, fragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.addToBackStack(null);
			ft.commit();
		}
		return false;
	}
}

