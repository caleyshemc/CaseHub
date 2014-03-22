package greenie;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;

public class Route {
	
	private String tag;
	private String title;
	private String shortTitle;
	private String color;
	private String oppositeColor;
	private LatLng minLatLng;
	private LatLng maxLatLng;
	private ArrayList<Direction> directions;
	private ArrayList<Path> paths;
	
	public Route() {
		// TODO Auto-generated constructor stub
	}
	
	public Route(String tag, String title, String shortTitle, String color,
			String oppositeColor, LatLng minLatLng, LatLng maxLatLng,
			ArrayList<Direction> directions, ArrayList<Path> paths) {
		this.tag = tag;
		this.title = title;
		this.shortTitle = shortTitle;
		this.color = color;
		this.oppositeColor = oppositeColor;
		this.minLatLng = minLatLng;
		this.maxLatLng = maxLatLng;
		this.directions = directions;
		this.paths = paths;
	}
	
	public void getRouteData(String routeTag){
		/*WebClient webClient = new WebClient();
		String url = "http://www.nextbus.com/googleMap/?a=case-western&r=" + routeTag;
		HtmlPage currentPage;
		try {
			currentPage = webClient.getPage(url);
			ScriptResult result = currentPage.executeJavaScript("loadRouteData('" + routeTag + "')");
			System.out.print(result.toString());
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getOppositeColor() {
		return oppositeColor;
	}

	public void setOppositeColor(String oppositeColor) {
		this.oppositeColor = oppositeColor;
	}

	public LatLng getMinLatLng() {
		return minLatLng;
	}

	public void setMinLatLng(LatLng minLatLng) {
		this.minLatLng = minLatLng;
	}

	public LatLng getMaxLatLng() {
		return maxLatLng;
	}

	public void setMaxLatLng(LatLng maxLatLng) {
		this.maxLatLng = maxLatLng;
	}

	public ArrayList<Direction> getDirections() {
		return directions;
	}

	public void setDirections(ArrayList<Direction> directions) {
		this.directions = directions;
	}

	public ArrayList<Path> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}

	public class Stop{
		private String tag;
		private String title;
		private LatLng latlng;
		
		public Stop(){
			this.tag = "";
			this.title = "";
			this.latlng = new LatLng(0.0f, 0.0f);
		}
		
		public Stop(String tag, String title, LatLng latlng){
			this.tag = tag;
			this.title = title;
			this.latlng = latlng;
		}
		
		public String getTag(){
			return tag;
		}

		public void setTag(String tag){
			this.tag = tag;
		}

		public String getTitle(){
			return title;
		}

		public void setTitle(String title){
			this.title = title;
		}

		public LatLng getLatlng(){
			return latlng;
		}

		public void setLatlng(LatLng latlng){
			this.latlng = latlng;
		}
		
		public double getLat(){
			return this.latlng.latitude;
		}
		
		public double getLng(){
			return this.latlng.longitude;
		}
		
	}
	
	public class Direction{
		private String tag;
		private String title;
		private ArrayList<Stop> stops;
		
		public Direction(String tag, String title, ArrayList<Stop> stops) {
			this.tag = tag;
			this.title = title;
			this.stops = stops;
		}

		public String getTag(){
			return tag;
		}

		public void setTag(String tag){
			this.tag = tag;
		}

		public String getTitle(){
			return title;
		}

		public void setTitle(String title){
			this.title = title;
		}

		public ArrayList<Stop> getStops(){
			return stops;
		}

		public void setStops(ArrayList<Stop> stops){
			this.stops = stops;
		}
		
		public void addStop(Stop stop){
			this.stops.add(stop);
		}
		
		public int numStops(){
			return this.stops.size();
		}
	}

	public class Path{
		private ArrayList<LatLng> points;

		public Path(ArrayList<LatLng> points) {
			this.points = points;
		}

		public ArrayList<LatLng> getPoints() {
			return points;
		}

		public void setPoints(ArrayList<LatLng> points) {
			this.points = points;
		}
		
		public void addPoint(LatLng point){
			this.points.add(point);
		}
		
		public int numPoints(){
			return this.points.size();
		}
	}
}