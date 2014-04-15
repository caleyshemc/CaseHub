package greenie;

import greenie.Route.Stop;

import java.util.ArrayList;

import casehub.CaseHubDbHelper;
import casehub.MainActivity;
import casehub.CaseHubContract.FavoriteStopEntry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class GetFavoritesTask extends AsyncTask<ArrayList<Stop>, Void, ArrayList<Stop>> {

	private ArrayList<Stop> findFavorites(ArrayList<Stop> stops){
		ArrayList<Stop> favStops = new ArrayList<Stop>();
		CaseHubDbHelper dbHelper = new CaseHubDbHelper(MainActivity.c);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + FavoriteStopEntry.TABLE_NAME,null);
		if (c.moveToFirst()) {
            while (c.isAfterLast() == false) {
                String stopTag = c.getString(c.getColumnIndex(FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG));
                for(Stop curStop : stops){
                	if(curStop.getTag().equals(stopTag)){
                		favStops.add(curStop);
                	}
                }
                c.moveToNext();
            }
        }
		return favStops;
	}
	
	@Override
	protected ArrayList<Stop> doInBackground(ArrayList<Stop>... params) {
		return findFavorites(params[0]);
	}

}
