package map;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.casehub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.os.Bundle;
import android.app.Fragment;
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
			parseJSON();
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
			}
		}
	}
	
	public void parseJSON(){
		try {
			cMap = new ParseMapJSONTask().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}

