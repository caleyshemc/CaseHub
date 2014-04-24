package map;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.android.gms.maps.model.LatLng;

import casehub.CaseHubContract.CampusMapSubgroup;
import casehub.CaseHubContract.CampusMapType;
import casehub.MainActivity;
import casehub.CaseHubContract.CampusMapPoint;
import map.CaseMap.MapType;
import map.CaseMap.Point;
import map.CaseMap.Subgroup;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class MapDBReadTask extends AsyncTask<Void, Void, CaseMap> {

	private CaseMap readDBToBean(){
		CaseMap cMap = new CaseMap();
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();

		String[] pProjection = {CampusMapPoint.COL_ADDRESS, 
				CampusMapPoint.COL_ENTITIES,
				CampusMapPoint.COL_EXTRA_NAMES,
				CampusMapPoint.COL_IMAGE,
				CampusMapPoint.COL_LAT,
				CampusMapPoint.COL_LNG,
				CampusMapPoint.COL_LDAP,
				CampusMapPoint.COL_NAME,
				CampusMapPoint.COL_NUM,
				CampusMapPoint.COL_SIS,
				CampusMapPoint.COL_TYPE_ID,
				CampusMapPoint.COL_URL,
				CampusMapPoint.COL_ZONE
		};
		
		String[] sProjection = { CampusMapSubgroup.COL_ID,
				CampusMapSubgroup.COL_LAT,
				CampusMapSubgroup.COL_LNG,
				CampusMapSubgroup.COL_NAME,
				CampusMapSubgroup.COL_TYPE_ID
		};
		
		String[] tProjection = { CampusMapType.COL_NAME,
				CampusMapType.COL_TYPE_ID,
				CampusMapType.COL_COLOR
		};

		Cursor c = db.query(CampusMapPoint.TABLE_NAME, pProjection, null, null, null, null, null);

		int addr_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_ADDRESS);
		int ent_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_ENTITIES);
		int exna_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_EXTRA_NAMES);
		int img_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_IMAGE);
		int lat_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_LAT);
		int lng_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_LNG);
		int ldap_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_LDAP);
		int name_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_NAME);
		int num_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_NUM);
		int sis_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_SIS);
		int tid_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_TYPE_ID);
		int url_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_URL);
		int zone_index = c.getColumnIndexOrThrow(CampusMapPoint.COL_ZONE);

		if(!c.moveToFirst()){
			//Cursor is empty
			return null;
		}

		do{
			ArrayList<String> ldap = new ArrayList<String>();
			ArrayList<String> exna = new ArrayList<String>();
			ArrayList<String> ent = new ArrayList<String>();
			
			if(c.getString(ldap_index) != null){
				ldap = new ArrayList<String>(Arrays.asList(c.getString(ldap_index).split(",")));
			}
			
			if(c.getString(exna_index) != null){
				exna = new ArrayList<String>(Arrays.asList(c.getString(exna_index).split(",")));
			}
			
			if(c.getString(ent_index) != null){
				ent = new ArrayList<String>(Arrays.asList(c.getString(ent_index).split(",")));
			}
			
			Point tempPoint = cMap.new Point(c.getString(num_index), 
					c.getString(name_index), 
					c.getString(addr_index), 
					new LatLng(c.getDouble(lat_index), c.getDouble(lng_index)), 
					c.getString(sis_index), 
					c.getString(img_index), 
					c.getString(url_index), 
					c.getInt(tid_index), 
					c.getInt(zone_index), 
					ldap, 
					exna, 
					ent);

			cMap.addPoint(tempPoint.getName(), tempPoint);
		}while(c.moveToNext());
		
		c = db.query(CampusMapSubgroup.TABLE_NAME, sProjection, null, null, null, null, null);

		int id_index = c.getColumnIndexOrThrow(CampusMapSubgroup.COL_ID);
		lat_index = c.getColumnIndexOrThrow(CampusMapSubgroup.COL_LAT);
		lng_index = c.getColumnIndexOrThrow(CampusMapSubgroup.COL_LNG);
		name_index = c.getColumnIndexOrThrow(CampusMapSubgroup.COL_NAME);
		tid_index = c.getColumnIndexOrThrow(CampusMapSubgroup.COL_TYPE_ID);
		
		if(!c.moveToFirst()){
			//Cursor is empty
			return null;
		}
		
		do{
			Subgroup tempSub = cMap.new Subgroup(c.getInt(id_index), 
					c.getString(name_index), 
					c.getInt(tid_index), 
					new LatLng(c.getDouble(lat_index), c.getDouble(lng_index)));
			
			cMap.addSubgroup(tempSub);
		}while(c.moveToNext());
		
		c = db.query(CampusMapType.TABLE_NAME, tProjection, null, null, null, null, null);
		name_index = c.getColumnIndexOrThrow(CampusMapType.COL_NAME);
		tid_index = c.getColumnIndexOrThrow(CampusMapType.COL_TYPE_ID);
		int col_index = c.getColumnIndexOrThrow(CampusMapType.COL_COLOR);
		
		if(!c.moveToFirst()){
			//Cursor is empty
			return null;
		}
		
		do{
			MapType tempMType = cMap.new MapType(c.getInt(tid_index), 
					c.getString(name_index), 
					c.getString(col_index));
			
			cMap.addMapType(tempMType);
		}while(c.moveToNext());
		
		db.close();

		return cMap;
	}

	@Override
	protected CaseMap doInBackground(Void... params) {
		return readDBToBean();
	}

}
