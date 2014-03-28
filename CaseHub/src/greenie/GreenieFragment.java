package greenie;

import greenie.Route.Path;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GreenieFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener {
    
    static final CameraPosition CASE =
            new CameraPosition.Builder().target(new LatLng(41.5052695, -81.6082641))
                    .zoom(15.5f)
                    .build();
    
    
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
        .setFastestInterval(50)    // 16ms = 60fps
        .setPriority(LocationRequest.PRIORITY_LOW_POWER);

private static final String TAG = "lol";
private ArrayList<LatLng> path = new ArrayList<LatLng>();

@Override
public void onResume() {
    super.onResume();
    setUpMapIfNeeded();
    setUpLocationClientIfNeeded();
    mLocationClient.connect();
}

private static View view;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    // Inflate the layout for this fragment
	if (view != null) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
            parent.removeView(view);
    }
    try {
        view = inflater.inflate(R.layout.fragment_greenie, container, false);
    } catch (InflateException e) {
        /* map is already there, just return view as it is */
    } finally {
    	Spinner rSpinner = (Spinner) view.findViewById(R.id.routeSpinner);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.c,
    	        R.array.routes_array, android.R.layout.simple_spinner_item);
    	// Specify the layout to use when the list of choices appears
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	// Apply the adapter to the spinner
    	rSpinner.setAdapter(adapter);
    	Spinner sSpinner = (Spinner) view.findViewById(R.id.stopSpinner);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	adapter = ArrayAdapter.createFromResource(MainActivity.c,
    	        R.array.stops_array, android.R.layout.simple_spinner_item);
    	// Specify the layout to use when the list of choices appears
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	// Apply the adapter to the spinner
    	sSpinner.setAdapter(adapter);
    }
    
    return view;
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
            Button btn = (Button) view.findViewById(R.id.locationButton); //your button
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
            mMap.setPadding(0, 130, 0, 85);
        }
    }
}

private void setUpMap() {
	// Fetch and display selected route
	Route route = new Route();
	route.getRouteData("circlelink");
	AssetManager assetManager = getResources().getAssets();
	
    InputStream tinstr = null;
    try {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        tinstr = assetManager.open("nextbusSetupMap.xml");
        parser.setInput(new InputStreamReader(tinstr));
        xmlParse xparser = new xmlParse();
        route = xparser.parse(parser);
        
    } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    } catch (XmlPullParserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();		
	}
	
	drawRoute(route);
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
	for(int i = 0; i < route.numPaths(); i++){
		for(int j = 0; j < paths.get(i).numPoints() - 1; j++){
			LatLng src = paths.get(i).getPoint(j);
			LatLng dest = paths.get(i).getPoint(j + 1);
			Polyline line = mMap.addPolyline(new PolylineOptions() //mMap is the Map Object
			.add(new LatLng(src.latitude, src.longitude),
					new LatLng(dest.latitude,dest.longitude))
					.width(8).color(Color.BLUE).geodesic(true));
		}
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
}

