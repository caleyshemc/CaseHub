package greenie;

import greenie.Route.Direction;
import greenie.Route.Path;
import greenie.Route.Stop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.res.AssetManager;
import android.os.AsyncTask;

import casehub.MainActivity;

import com.google.android.gms.maps.model.LatLng;

public class XmlParseTask extends AsyncTask<Void, Void, ArrayList<Route>>{
	
	ArrayList<Route> routes = new ArrayList<Route>();
	
	private void parse() throws XmlPullParserException, IOException{
		Map<String,String> attributes = null;
		Route route = new Route();
		ArrayList<Stop> tempStops = new ArrayList<Stop>();
		Direction tempDir = route.new Direction();
		Path tempPath = route.new Path();
		AssetManager assetManager = MainActivity.c.getResources().getAssets();
		String[] list = {};

		try {
			list = assetManager.list("routes");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(list.length != 0){
			for(int i = 0; i < list.length; i++){
				InputStream tinstr = null;
				try {
					XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					tinstr = assetManager.open("routes/" + list[i]);
					xpp.setInput(new InputStreamReader(tinstr));
					
					int eventType = xpp.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						String tagname = xpp.getName();
						switch(eventType){
						case XmlPullParser.START_DOCUMENT:
							break;
						case XmlPullParser.END_DOCUMENT:
							break;
						case XmlPullParser.START_TAG:
							if(tagname.equalsIgnoreCase("route")){
								try {
									attributes = getAttributes(xpp);
									route.setTag(attributes.get("tag"));
									route.setTitle(attributes.get("title"));
									route.setShortTitle(attributes.get("shortTitle"));
									route.setColor(attributes.get("color"));
									route.setOppositeColor(attributes.get("oppositeColor"));
									LatLng min = new LatLng(Double.parseDouble(attributes.get("latMin")), Double.parseDouble(attributes.get("lonMin")));
									LatLng max = new LatLng(Double.parseDouble(attributes.get("latMax")), Double.parseDouble(attributes.get("lonMax")));
									route.setMinLatLng(min);
									route.setMaxLatLng(max);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else if(tagname.equalsIgnoreCase("stop")){
								if(xpp.getAttributeCount() > 1){
									try {
										attributes = getAttributes(xpp);
										Stop tempStop = route.new Stop();
										tempStop.setTag(attributes.get("tag"));
										tempStop.setTitle(attributes.get("title"));
										tempStop.setLatlng(new LatLng(Double.parseDouble(attributes.get("lat")),Double.parseDouble(attributes.get("lon"))));
										tempStops.add(tempStop);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}else{
									String stopTag = xpp.getAttributeValue(0);
									for(int j = 0; j < tempStops.size(); j++){
										if(tempStops.get(j).getTag().equalsIgnoreCase(stopTag)){
											tempStops.get(j).setDir(tempDir.getTag());
											tempDir.addStop(tempStops.get(j));
											break;
										}
									}
								}
							}else if(tagname.equalsIgnoreCase("direction")){
								try {
									attributes = getAttributes(xpp);
									tempDir.setTag(attributes.get("tag"));
									tempDir.setTitle(attributes.get("title"));
									tempDir.setUseForUI(Boolean.parseBoolean(attributes.get("useForUI")));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else if(tagname.equals("point")){
								tempPath.addPoint(new LatLng(Double.parseDouble(xpp.getAttributeValue(0)), Double.parseDouble(xpp.getAttributeValue(1))));
							}
							break;
						case XmlPullParser.END_TAG:
							if(tagname.equalsIgnoreCase("direction")){
								//on the close tag, add the tempDir to the route and reinitialize it for the next direction
								route.addDirection(tempDir);
								tempDir = route.new Direction();
							}else if(tagname.equalsIgnoreCase("path")){
								//on the close tag, add the tempPath to the route and reinitialize it for the next path
								route.addPath(tempPath);
								tempPath = route.new Path();
							}else if(tagname.equalsIgnoreCase("route")){
								//on the close tag, add the route to routes and reinitialize it for the next route
								routes.add(route);
								route = new Route();
							}
							break;
						case XmlPullParser.TEXT:
							break;
						}
						eventType = xpp.next();
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();		
				}
			}
		}		
	}

	private Map<String,String>  getAttributes(XmlPullParser parser) throws Exception {
		Map<String,String> attrs=null;
		int acount=parser.getAttributeCount();
		if(acount != -1) {
			attrs = new HashMap<String,String>(acount);
			for(int x = 0;x < acount;x++) {
				attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
			}
		}
		else {
			throw new Exception("No attributes");
		}
		return attrs;
	}

	@Override
	protected ArrayList<Route> doInBackground(Void... params) {
		try {
			parse();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return routes;
	}
}
