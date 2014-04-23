package map;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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
	
	
	public class Point{
		private Marker marker;
		private String num;
		private String name;
		private String address;
		private LatLng coord;
		private String sis;
		private String image;
		private String url;
		private int type_id;
		private int zone;
		private ArrayList<String> ldap;
		private ArrayList<String> extra_names;
		private ArrayList<String> entities;
		
		public Point(){
			
		}
		
		public Point(String num, String name, String address, LatLng coord,
				String sis, String image, String url, int type_id, int zone,
				ArrayList<String> ldap, ArrayList<String> extra_names,
				ArrayList<String> entities) {
			this.num = num;
			this.name = name;
			this.address = address;
			this.coord = coord;
			this.sis = sis;
			this.image = image;
			this.url = url;
			this.type_id = type_id;
			this.zone = zone;
			this.ldap = ldap;
			this.extra_names = extra_names;
			this.entities = entities;
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
		public int getType_id() {
			return type_id;
		}
		public void setType_id(int type_id) {
			this.type_id = type_id;
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
		public ArrayList<String> getExtra_names() {
			return extra_names;
		}
		public void setExtra_names(ArrayList<String> extra_names) {
			this.extra_names = extra_names;
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
		private int type_id;
		private LatLng coord;
		
		public Subgroup(){
			
		}

		public Subgroup(int id, String name, int type_id, LatLng coord) {
			this.id = id;
			this.name = name;
			this.type_id = type_id;
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

		public int getType_id() {
			return type_id;
		}

		public void setType_id(int type_id) {
			this.type_id = type_id;
		}

		public LatLng getCoord() {
			return coord;
		}

		public void setCoord(LatLng coord) {
			this.coord = coord;
		}
	}

	public class MapType{
		private int type_id;
		private String name;
		private float color;
	}
}
