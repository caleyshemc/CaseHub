package greenie;

import greenie.Route.Direction;
import greenie.Route.Path;
import greenie.Route.Stop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import casehub.MainActivity;

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
import android.content.res.AssetManager;
import android.graphics.Color;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class GreenieFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener, OnMarkerClickListener, OnItemSelectedListener {

	private GoogleMap mMap;
	private LocationClient mLocationClient;
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(50)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_LOW_POWER);
	Route currentRoute = new Route();
	ArrayList<Route> routes = new ArrayList<Route>();
	ArrayList<Stop> stops = new ArrayList<Stop>();

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

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
			Spinner rSpinner = (Spinner) mView.findViewById(R.id.routeSpinner);
			rSpinner.setOnItemSelectedListener(this);
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.c,
					R.array.routes_array, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			rSpinner.setAdapter(adapter);
			Spinner sSpinner = (Spinner) mView.findViewById(R.id.stopSpinner);
			sSpinner.setOnItemSelectedListener(this);
			loadRoutes();
			currentRoute = routes.get(0);
		}

		return mView;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				mMap.setOnMarkerClickListener(this);
				Button btn = (Button) mView.findViewById(R.id.locationButton); //your button
				btn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Location location = mLocationClient.getLastLocation();
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new    
								LatLng(location.getLatitude(),     
										location.getLongitude()), 15.5f));
					}
				});
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mMap.setPadding(0, 130, 0, 85);
		drawRoute(currentRoute);
		drawStops(currentRoute);
	}


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
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.5f);
		mMap.animateCamera(cameraUpdate);
	}

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
		//Do nothing
	}

	@Override
	public void onDisconnected() {
		//Do nothing
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
			for(int i = 0; i < stops.size(); i++){
				if(stops.get(i).getMarker().equals(marker)){
					Spinner sSpinner = (Spinner) mView.findViewById(R.id.stopSpinner);
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
			for(int i = 0; i < routes.size(); i++){
				if(parent.getItemAtPosition(position).toString().equals(routes.get(i).getTitle())){
					currentRoute = routes.get(i);
					stops = currentRoute.getAllStops();
					mMap.clear();
					drawRoute(currentRoute);
					drawStops(currentRoute);
					String[] stops = currentRoute.listStops();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.c, android.R.layout.simple_spinner_dropdown_item, stops);
					Spinner sSpinner = (Spinner) mView.findViewById(R.id.stopSpinner);
					sSpinner.setAdapter(adapter);
					break;
				}
			}
			break;
		case R.id.stopSpinner:
			String selectedItem = parent.getItemAtPosition(position).toString();
			for(int i = 0; i < currentRoute.getDirections().size(); i++){
				for(int j = 0; j < currentRoute.getDirections().get(i).numStops(); j++){
					Stop tempStop = currentRoute.getDirections().get(i).getStop(j);
					if(selectedItem.equals(tempStop.getTitle())){
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempStop.getMarker().getPosition(), 17f));
						TextView text = (TextView) mView.findViewById(R.id.predictions);
						String pred = getPrediction(currentRoute.getTag(), currentRoute.getDirections().get(i).getTag(), tempStop.getTag());
						text.setText(pred);	
						break;
					}
				}
			}
			//Update prediction for that stop
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//Do nothing

	}
	
	public String getPrediction(String route, String direction, String stop){
		String pred = "";
		try {
			pred = new PredictionTask().execute(route, direction, stop).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pred;
	}

	public void loadRoutes(){
		try {
			routes = new XmlParseTask().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

