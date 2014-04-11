package greenie;

import java.util.ArrayList;

import greenie.Route.Stop;
import android.location.Location;
import android.os.AsyncTask;

public class NearestStopTask extends AsyncTask<Void, Void, Stop> {
	private Location location = new Location("");
	private ArrayList<Stop> stops = new ArrayList<Stop>();
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public void setStops(ArrayList<Stop> stops) {
		this.stops = stops;
	}

	private Stop findNearestStop(){
		Location stopLoc = new Location("");
		Float dist = 10000f;
		Stop nearest = null;
		for(Stop curStop : stops){
			stopLoc.setLatitude(curStop.getLat());
			stopLoc.setLongitude(curStop.getLng());
			if(location.distanceTo(stopLoc) < dist){
				dist = location.distanceTo(stopLoc);
				nearest = curStop;
			}
		}
		
		return nearest;
	}

	@Override
	protected Stop doInBackground(Void... params) {
		return findNearestStop();
	}
}