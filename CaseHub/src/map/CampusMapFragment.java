package map;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import map.CaseMap.Point;

import com.casehub.R;
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
import android.app.Fragment;
import android.content.SharedPreferences;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CampusMapFragment extends Fragment implements OnMarkerClickListener {
	
	private GoogleMap mMap;
	private CaseMap cMap = new CaseMap();
	private static View mView;
    static final CameraPosition CASE =
            new CameraPosition.Builder().target(new LatLng(41.5052695, -81.6082641))
                    .zoom(17)
                    .build();
    public static final String DB_STATE_PREF = "DatabaseStateFile";
    public static final String DB_STATE = "DatabaseState=";
    public static final int DB_FULL = 1;
    public static final int DB_EMPTY = 0;
	
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
			SharedPreferences settings = getActivity().getSharedPreferences(DB_STATE_PREF, 0);
			//If the database is empty, fill it and the databean and mark it filled, otherwise fill the databean from the database
			if(settings.getInt(DB_STATE, 0) == 0){
				parseJSON();
				writeDataBean();
				settings.edit().putInt(DB_STATE, DB_FULL).commit();
			}else{
				buildDataBean();
			}
			int points = cMap.numPoints();
			setUpMapIfNeeded();
		}
        return mView;
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
		for(Point curPoint : cMap.getPoints().values()){
			float color = 0f;
			switch(curPoint.getTypeId()){
			case 0:
				color = 120; //Green
				break;
			case 1:
				color = 240; //Blue
				break;
			case 2:
				color = 0; //Red
				break;
			case 3:
				color = 30; //Orange
				break;
			case 4:
				color = 270; //Violet
				break;
			case 5:
				color = 300; //Magenta
				break;
			case 6:
				color = 180; //Cyan
				break;
			case 7:
				color = 70; //Yellow
				break;
			case 8:
				color = 210; //Azure
				break;
			}	
			Marker point = mMap.addMarker(new MarkerOptions()
			.position(curPoint.getCoord())
			.title(curPoint.getName())
			.icon(BitmapDescriptorFactory.defaultMarker(color)));
			curPoint.setMarker(point);
		}
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}

