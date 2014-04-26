package greenie;

import greenie.Route.Path;
import greenie.Route.Stop;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import casehub.CaseHubDbHelper;
import casehub.MainActivity;
import casehub.CaseHubContract.FavoriteStopEntry;

import com.casehub.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GreenieFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener, OnMarkerClickListener, OnItemSelectedListener {

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(50)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_LOW_POWER);
	Route currentRoute = new Route();
	Stop currentStop = currentRoute.new Stop();
	ArrayList<Route> routes = new ArrayList<Route>();
	ArrayList<Stop> stops = new ArrayList<Stop>();
	ArrayList<Stop> favStops = new ArrayList<Stop>();
	boolean showFavorites = false;
	Spinner sSpinner;
	Spinner rSpinner;

	private static View mView;
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
			mView = inflater.inflate(R.layout.fragment_greenie, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		} finally {
			setHasOptionsMenu(true);
			rSpinner = (Spinner) mView.findViewById(R.id.routeSpinner);
			sSpinner = (Spinner) mView.findViewById(R.id.stopSpinner);
			rSpinner.setOnItemSelectedListener(this);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.c,
					R.array.routes_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			rSpinner.setAdapter(adapter);
			sSpinner.setOnItemSelectedListener(this);
			ToggleButton fav = (ToggleButton) mView.findViewById(R.id.fav);
			fav.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onFavButtonClick(v);
				}
			});
			loadRoutes();
			currentRoute = routes.get(0);
		}

		return mView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.greenie, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_fav:
			if(showFavorites){
				showFavorites = false;
				item.setIcon(R.drawable.starlight);
				getFavoriteStops();
				toggleFavStops(showFavorites);
			}else{
				showFavorites = true;
				item.setIcon(R.drawable.staryellow);
				getFavoriteStops();
				toggleFavStops(showFavorites);
			}

			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	//Instantiates the Google Map if it isn't already as well as sets other properties of the map
	private void setUpMapIfNeeded() {
		//Null check to ensure the map has not yet been instantiated
		if (mMap == null) {
			// Try to obtain the map
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if success
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				mMap.setOnMarkerClickListener(this);
				Button btn = (Button) mView.findViewById(R.id.locationButton); //your button
				btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Location location = mLocationClient.getLastLocation();
						Stop nearestStop = getNearestStop(location);
						sSpinner.setSelection(stops.indexOf(nearestStop));
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearestStop.getLatlng(), 17));
					}
				});
				mMap.setPadding(0, 130, 0, 85);
				drawRoute(currentRoute);
				drawStops(currentRoute);
			}
		}
	}

	//Sets up location client to get user's location
	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(
					MainActivity.c,
					this,  // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}


	/**
	 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(
				REQUEST,
				this);  // LocationListener
		Location location = mLocationClient.getLastLocation();
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
		mMap.animateCamera(cameraUpdate);
	}

	//Draws a polyline route from the passed route's paths
	public void drawRoute(Route route){
		ArrayList<Path> paths = route.getPaths();
		String sColor = "#" + currentRoute.getColor();
		int color = Color.parseColor(sColor);
		for(int i = 0; i < route.numPaths(); i++){
			for(int j = 0; j < paths.get(i).numPoints() - 1; j++){
				LatLng src = paths.get(i).getPoint(j);
				LatLng dest = paths.get(i).getPoint(j + 1);
				Polyline line = mMap.addPolyline(new PolylineOptions() //mMap is the Map Object
				.add(new LatLng(src.latitude, src.longitude),
						new LatLng(dest.latitude,dest.longitude))
						.width(8).geodesic(true).color(color));
			}
		}
	}

	//Places markers for each stop of passed route
	public void drawStops(Route route){
		for(int i = 0; i < stops.size(); i++){
			Marker stop = mMap.addMarker(new MarkerOptions().position(stops.get(i).getLatlng()));
			stops.get(i).setMarker(stop);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		//Do nothing
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(MainActivity.c, "Location connection failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		//Do nothing
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for(int i = 0; i < stops.size(); i++){
			if(stops.get(i).getMarker().equals(marker)){
				sSpinner.setSelection(i);
				TextView text = (TextView) mView.findViewById(R.id.predictions);
				String pred = getPrediction(currentRoute.getTag(), stops.get(i).getDir(), stops.get(i).getTag());
				text.setText(pred);
				break;
			}
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch(parent.getId()){
		case R.id.routeSpinner:
			//if route is changed, update current route as well as stop spinner
			for(int i = 0; i < routes.size(); i++){
				if(parent.getItemAtPosition(position).toString().equals(routes.get(i).getTitle())){
					currentRoute = routes.get(i);
					stops = currentRoute.getAllStops();
					mMap.clear();
					drawRoute(currentRoute);
					drawStops(currentRoute);
					String[] stops = currentRoute.listStops();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.c, android.R.layout.simple_spinner_dropdown_item, stops);
					sSpinner.setAdapter(adapter);
					break;
				}
			}
			break;
		case R.id.stopSpinner:
			//if stop is changed, update current stop, animate to its location, update predictions
			String selectedItem = parent.getItemAtPosition(position).toString();
			for(int i = 0; i < currentRoute.getDirections().size(); i++){
				for(int j = 0; j < currentRoute.getDirections().get(i).numStops(); j++){
					Stop tempStop = currentRoute.getDirections().get(i).getStop(j);
					if(selectedItem.equals(tempStop.getTitle())){
						currentStop = tempStop;
						boolean favorite = isStopFavorite(currentStop);
						ToggleButton fav = (ToggleButton) mView.findViewById(R.id.fav);
						fav.setChecked(favorite);
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempStop.getMarker().getPosition(), 17));
						TextView text = (TextView) mView.findViewById(R.id.predictions);
						String pred = getPrediction(currentRoute.getTag(), currentRoute.getDirections().get(i).getTag(), tempStop.getTag());
						text.setText(pred);	
						break;
					}
				}
			}
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//Do nothing

	}

	//Call to AsyncTask to get prediction
	public String getPrediction(String route, String direction, String stop){
		String pred = "";
		try {
			pred = new PredictionTask().execute(route, direction, stop).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return pred;
	}

	//Call to AsyncTask to populate DataBean from stored XML
	public void loadRoutes(){
		try {
			routes = new XmlParseTask().execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	//Call to AsyncTask to get nearest stop to location
	public Stop getNearestStop(Location location){
		Stop tempStop = currentRoute.new Stop();
		NearestStopTask task = new NearestStopTask();
		task.setLocation(location);
		task.setStops(stops);
		try {
			tempStop = task.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return tempStop;
	}

	//Call to AsyncTask to check if stop is a favorite
	private boolean isStopFavorite(Stop stop) {
		boolean favorite = false;
		try {
			favorite = new FavoriteStopTask().execute(stop).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return favorite;
	}

	//Call to AsyncTask to get list of favorite stops
	@SuppressWarnings("unchecked")
	private void getFavoriteStops () {
		try {
			favStops = new GetFavoritesTask().execute(stops).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	//Display only favorite stops or show all
	//display == true means hide all but favorites
	//display == false means show all
	public void toggleFavStops(boolean display){
		for(Stop curStop : stops){
			if(!favStops.contains(curStop)){ //If this stop is not a favorite
				curStop.getMarker().setVisible(!display); //Hide it if display is true, show it if false
			}
		}
		if(display){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.c, android.R.layout.simple_spinner_dropdown_item, stopsToString(favStops));
			sSpinner.setAdapter(adapter);
		}else{
			String[] stops = currentRoute.listStops();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.c, android.R.layout.simple_spinner_dropdown_item, stops);
			sSpinner.setAdapter(adapter);
		}
	}

	//Get list of stops to update spinner
	public String[] stopsToString(ArrayList<Stop> stops){
		String[] stopsString = new String[stops.size()];
		for(int i = 0; i < stops.size(); i++){
			stopsString[i] = stops.get(i).getTitle();
		}
		return stopsString;
	}

	//Handle adding or removing a stop to favorites
	public void onFavButtonClick(View v){
		ToggleButton fav = (ToggleButton) mView.findViewById(R.id.fav);
		CaseHubDbHelper dbHelper = new CaseHubDbHelper(MainActivity.c);

		if(!fav.isChecked()){
			fav.setChecked(false);
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String selection = FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG + " LIKE ?";
			String[] selectionArgs = {currentStop.getTag()};
			db.delete(FavoriteStopEntry.TABLE_NAME, selection, selectionArgs);
		}else{
			fav.setChecked(true);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG, currentStop.getTag());
			db.insert(FavoriteStopEntry.TABLE_NAME, null, values);
		}
	}
}

