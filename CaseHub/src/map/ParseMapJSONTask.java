package map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import map.CaseMap.MapType;
import map.CaseMap.Point;
import map.CaseMap.Subgroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.os.AsyncTask;
import android.util.Log;

//AsyncTask to parse JSON from http://www.case.edu/maps/
//Returns a populated DataBean
public class ParseMapJSONTask extends AsyncTask<Void, Void, CaseMap> {

	JSONObject obj;
	CaseMap cMap = new CaseMap();

	public void fetch(URL url){
		try{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			InputStream stream = conn.getInputStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8")); 
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null){
				responseStrBuilder.append(inputStr);
			}
			obj = new JSONObject(responseStrBuilder.toString());
			stream.close();
			parse();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void parse() throws JSONException{
		JSONArray points = obj.getJSONArray("points");
		JSONArray subgroups = obj.getJSONArray("subgroups");
		JSONArray mapTypes = obj.getJSONArray("mapTypes");
		
		for(int i = 0; i < subgroups.length(); i++){
			Subgroup tempSub = cMap.new Subgroup();
			JSONObject subgroup = subgroups.getJSONObject(i);
			
			tempSub.setId(subgroup.optInt("subgroup_id"));
			tempSub.setName(subgroup.optString("subgroup_name"));
			tempSub.setCoord(new LatLng(subgroup.optDouble("lat"),subgroup.optDouble("lon")));
			tempSub.setTypeId(subgroup.optInt("type_id"));
			
			cMap.addSubgroup(tempSub);
		}
		
		for(int i = 0; i < mapTypes.length(); i++){
			MapType tempMapType = cMap.new MapType();
			JSONObject mapType = mapTypes.getJSONObject(i);
			
			tempMapType.setName(mapType.optString("name"));
			tempMapType.setTypeId(mapType.optInt("type_id"));
			tempMapType.setColor(mapType.optString("color"));
			
			cMap.addMapType(tempMapType);
		}

		for(int i = 0; i < points.length(); i++){
			Point tempPoint = cMap.new Point();
			JSONObject point = points.getJSONObject(i);

			JSONArray ldapA = point.optJSONArray("ldap");
			if(ldapA != null){
				String ldap = ldapA.join(",").replace("\"", "");
				tempPoint.setLdap(new ArrayList<String>(Arrays.asList(ldap.split(","))));
			}
			JSONArray extra_namesA =  point.optJSONArray("extra_names");
			if(extra_namesA != null){
				String extra_names = extra_namesA.join(",").replace("\"", "");
				tempPoint.setExtraNames(new ArrayList<String>(Arrays.asList(extra_names.split(","))));
			}
			JSONArray entitiesA = point.optJSONArray("entities");
			if(entitiesA != null){
				ArrayList<String> entities = new ArrayList<String>();
				for(int j = 0; j < entitiesA.length(); j++){
					entities.add(entitiesA.getJSONObject(j).optString("entity_name"));			
				}
				tempPoint.setEntities(entities);
			}

			JSONObject address = point.getJSONObject("address");
			StringBuilder addressBuilder = new StringBuilder();
			if(address.optString("street") != null)
				addressBuilder.append(address.optString("street") + ",");
			if(address.optString("city") != null)
				addressBuilder.append(address.optString("city") + ",");
			if(address.optString("state") != null)
				addressBuilder.append(address.optString("state") + ",");
			if(address.optString("zip") != null)
				addressBuilder.append(address.optString("zip"));
			tempPoint.setAddress(addressBuilder.toString());

			tempPoint.setNum(point.getString("num"));
			tempPoint.setName(point.getString("name"));
			tempPoint.setCoord(new LatLng(point.optDouble("lat"),point.optDouble("lon")));
			tempPoint.setSis(point.optString("sis"));
			tempPoint.setUrl(point.optString("url"));
			tempPoint.setImage(point.optString("image"));
			tempPoint.setTypeId(point.optInt("type_id"));
			tempPoint.setZone(point.optInt("zone"));
			cMap.addPoint(point.getString("name"), tempPoint);			
		}
	}

	@Override
	protected CaseMap doInBackground(Void... params) {
		try {
			fetch(new URL("https://webapps.case.edu/map-cgi/gmap_json.pl"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return cMap;
	}

}
