package map;

import casehub.CaseHubContract.CampusMapSubgroup;
import casehub.CaseHubContract.CampusMapType;
import casehub.CaseHubContract.ScheduleEventEntry;
import casehub.MainActivity;
import casehub.CaseHubContract.CampusMapPoint;
import map.CaseMap.MapType;
import map.CaseMap.Point;
import map.CaseMap.Subgroup;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class MapDBWriteTask extends AsyncTask<CaseMap, Void, Void> {

	private void writeBeanToDB(CaseMap cMap){
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + CampusMapPoint.TABLE_NAME , null);
		if(!c.moveToFirst()){
			for(Point curPoint : cMap.getPoints().values()){
				ContentValues values = new ContentValues();
				values.put(CampusMapPoint.COL_ADDRESS, curPoint.getAddress());
				if(curPoint.getEntities() != null)
					values.put(CampusMapPoint.COL_ENTITIES, curPoint.getEntities().toString().replace("[","").replace("]", ""));
				if(curPoint.getExtraNames() != null)
					values.put(CampusMapPoint.COL_EXTRA_NAMES, curPoint.getExtraNames().toString().replace("[","").replace("]", ""));
				values.put(CampusMapPoint.COL_IMAGE, curPoint.getImage());
				values.put(CampusMapPoint.COL_LAT, curPoint.getCoord().latitude);
				values.put(CampusMapPoint.COL_LNG, curPoint.getCoord().longitude);
				if(curPoint.getLdap() != null)
					values.put(CampusMapPoint.COL_LDAP, curPoint.getLdap().toString().replace("[","").replace("]", ""));
				values.put(CampusMapPoint.COL_NAME, curPoint.getName());
				values.put(CampusMapPoint.COL_NUM, curPoint.getNum());
				values.put(CampusMapPoint.COL_SIS, curPoint.getSis());
				values.put(CampusMapPoint.COL_TYPE_ID, curPoint.getTypeId());
				values.put(CampusMapPoint.COL_URL, curPoint.getUrl());
				values.put(CampusMapPoint.COL_ZONE, curPoint.getZone());

				db.insert(CampusMapPoint.TABLE_NAME, null, values);		
			}
			for(Subgroup curSub : cMap.getSubgroups()){
				ContentValues values = new ContentValues();
				values.put(CampusMapSubgroup.COL_ID, curSub.getId());
				values.put(CampusMapSubgroup.COL_LAT, curSub.getCoord().latitude);
				values.put(CampusMapSubgroup.COL_LNG, curSub.getCoord().longitude);
				values.put(CampusMapSubgroup.COL_NAME, curSub.getName());
				values.put(CampusMapSubgroup.COL_TYPE_ID, curSub.getTypeId());

				db.insert(CampusMapSubgroup.TABLE_NAME, null, values);
			}
			for(MapType curMap : cMap.getMapTypes()){
				ContentValues values = new ContentValues();
				values.put(CampusMapType.COL_NAME, curMap.getName());
				values.put(CampusMapType.COL_TYPE_ID, curMap.getTypeId());
				values.put(CampusMapType.COL_COLOR, curMap.getColor());

				db.insert(CampusMapType.TABLE_NAME, null, values);
			}
			c.close();
			db.close();
		}
	}

	@Override
	protected Void doInBackground(CaseMap... params) {
		writeBeanToDB(params[0]);
		return null;
	}

}
