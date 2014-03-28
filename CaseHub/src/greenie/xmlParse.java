package greenie;

import greenie.Route.Direction;
import greenie.Route.Path;
import greenie.Route.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.model.LatLng;

public class xmlParse{
	public xmlParse() {
		// TODO Auto-generated constructor stub
	}

	public Route parse(XmlPullParser xpp) throws XmlPullParserException, IOException{
		Map<String,String> attributes = null;
		Route route = new Route();
		ArrayList<Stop> tempStops = new ArrayList<Stop>();
		Direction tempDir = route.new Direction();
		Path tempPath = route.new Path();
		
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
						// TODO Auto-generated catch block
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						String stopTag = xpp.getAttributeValue(0);
						for(int i = 0; i < tempStops.size(); i++){
							if(tempStops.get(i).getTag().equalsIgnoreCase(stopTag)){
								tempDir.addStop(tempStops.get(i));
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
						// TODO Auto-generated catch block
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
				}
				break;
			case XmlPullParser.TEXT:
				break;
			}
			eventType = xpp.next();
		}
		return route;
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
}
