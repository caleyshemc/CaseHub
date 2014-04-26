package greenie;

import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//DataBean to store all details of a given Route
public class Route {
	
	private String tag;
	private String title;
	private String shortTitle;
	private String color;
	private String oppositeColor;
	private LatLng minLatLng;
	private LatLng maxLatLng;
	private ArrayList<Direction> directions = new ArrayList<Direction>();
	private ArrayList<Path> paths = new ArrayList<Path>();
	
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
	
	public int numDirections(){
		return this.directions.size();
	}
	
	public void addDirection(Direction direction){
		this.directions.add(direction);
	}

	public ArrayList<Path> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}
	
	public void addPath(Path path){
		this.paths.add(path);
	}
	
	public int numPaths(){
		return this.paths.size();
	}
	
	public ArrayList<Stop> getAllStops(){
		ArrayList<Stop> stops = new ArrayList<Stop>();
		for(int i = 0; i < this.directions.size(); i++){
			for(int j = 0; j < this.directions.get(i).numStops(); j++){
				stops.add(this.directions.get(i).getStop(j));
			}
		}
		return stops;
	}
	
	public String[] listStops(){
		ArrayList<Stop> stops = getAllStops();
		String[] stopList = new String[stops.size()];
		for(int i = 0; i < stops.size(); i++){
			stopList[i] = stops.get(i).getTitle();
		}
		return stopList;
	}

	public class Stop{
		private String tag;
		private String title;
		private String dir;
		private LatLng latlng;
		private Marker marker;
		
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

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
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

		public Marker getMarker() {
			return marker;
		}

		public void setMarker(Marker marker) {
			this.marker = marker;
		}
		
	}
	
	public class Direction{
		private String tag;
		private String title;
		private Boolean useForUI;
		private ArrayList<Stop> stops = new ArrayList<Stop>();
		
		public Direction(String tag, String title, Boolean useForUI, ArrayList<Stop> stops) {
			this.tag = tag;
			this.title = title;
			this.useForUI = useForUI;
			this.stops = stops;
		}

		public Direction() {
			this.tag = "";
			this.title = "";
			this.useForUI = false;
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

		public Boolean getUseForUI() {
			return useForUI;
		}

		public void setUseForUI(Boolean useForUI) {
			this.useForUI = useForUI;
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
		
		public Stop getStop(int i){
			return this.stops.get(i);
		}
	}

	public class Path{
		private ArrayList<LatLng> points = new ArrayList<LatLng>();

		public Path(ArrayList<LatLng> points) {
			this.points = points;
		}

		public Path() {
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
		
		public LatLng getPoint(int i){
			return this.points.get(i);
		}
		
		public int numPoints(){
			return this.points.size();
		}
	}
}