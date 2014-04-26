package map;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

//DataBean to store points, subgroups, and maptypes from http://www.case.edu/maps/
public class CaseMap {
	
	private HashMap<String, Point> points = new HashMap<String, Point>();
	private ArrayList<Subgroup> subgroups = new ArrayList<Subgroup>();
	private ArrayList<MapType> mapTypes = new ArrayList<MapType>();
	
	public CaseMap() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPoint(String key, Point value){
		this.points.put(key, value);
	}
	
	public void addSubgroup(Subgroup subgroup){
		this.subgroups.add(subgroup);
	}
	
	public void addMapType(MapType mapType){
		this.mapTypes.add(mapType);
	}
	
	public int numPoints(){
		return this.points.size();
	}
	
	public HashMap<String, Point> getPoints() {
		return points;
	}
	
	public Point getPoint(String key){
		return points.get(key);
	}

	public void setPoints(HashMap<String, Point> points) {
		this.points = points;
	}

	public ArrayList<Subgroup> getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(ArrayList<Subgroup> subgroups) {
		this.subgroups = subgroups;
	}

	public ArrayList<MapType> getMapTypes() {
		return mapTypes;
	}

	public void setMapTypes(ArrayList<MapType> mapTypes) {
		this.mapTypes = mapTypes;
	}


	public class Point{
		private Marker marker;
		private String num;
		private String name;
		private String address;
		private LatLng coord;
		private String sis;
		private String image;
		private String url;
		private int typeId;
		private int zone;
		private ArrayList<String> ldap;
		private ArrayList<String> extraNames;
		private ArrayList<String> entities;
		
		public Point(){
			
		}
		
		public Point(String num, String name, String address, LatLng coord,
				String sis, String image, String url, int typeId, int zone,
				ArrayList<String> ldap, ArrayList<String> extraNames,
				ArrayList<String> entities) {
			this.num = num;
			this.name = name;
			this.address = address;
			this.coord = coord;
			this.sis = sis;
			this.image = image;
			this.url = url;
			this.typeId = typeId;
			this.zone = zone;
			this.ldap = ldap;
			this.extraNames = extraNames;
			this.entities = entities;
		}

		public Marker getMarker() {
			return marker;
		}

		public void setMarker(Marker marker) {
			this.marker = marker;
		}

		public String getNum() {
			return num;
		}
		public void setNum(String num) {
			this.num = num;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public LatLng getCoord() {
			return coord;
		}
		public void setCoord(LatLng coord) {
			this.coord = coord;
		}
		public String getSis() {
			return sis;
		}
		public void setSis(String sis) {
			this.sis = sis;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public int getTypeId() {
			return typeId;
		}
		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}
		public int getZone() {
			return zone;
		}
		public void setZone(int zone) {
			this.zone = zone;
		}
		public ArrayList<String> getLdap() {
			return ldap;
		}
		public void setLdap(ArrayList<String> ldap) {
			this.ldap = ldap;
		}
		public ArrayList<String> getExtraNames() {
			return extraNames;
		}
		public void setExtraNames(ArrayList<String> extraNames) {
			this.extraNames = extraNames;
		}
		public ArrayList<String> getEntities() {
			return entities;
		}
		public void setEntities(ArrayList<String> entities) {
			this.entities = entities;
		}
		
	}

	public class Subgroup{
		private int id;
		private String name;
		private int typeId;
		private LatLng coord;
		
		public Subgroup(){
			
		}

		public Subgroup(int id, String name, int typeId, LatLng coord) {
			this.id = id;
			this.name = name;
			this.typeId = typeId;
			this.coord = coord;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getTypeId() {
			return typeId;
		}

		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}

		public LatLng getCoord() {
			return coord;
		}

		public void setCoord(LatLng coord) {
			this.coord = coord;
		}
	}

	public class MapType{
		private int typeId;
		private String name;
		private String color;
		
		public MapType(){
			
		}
		
		public MapType(int typeId, String name, String color) {
			this.typeId = typeId;
			this.name = name;
			this.color = color;
		}
		public int getTypeId() {
			return typeId;
		}
		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
	}
}
