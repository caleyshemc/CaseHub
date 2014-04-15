package greenie;

import casehub.CaseHubContract.FavoriteStopEntry;
import casehub.CaseHubDbHelper;
import casehub.MainActivity;
import greenie.Route.Stop;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class FavoriteStopTask extends AsyncTask<Stop, Void, Boolean> {

	private boolean isFavoriteStop(Stop stop){
		CaseHubDbHelper dbHelper = new CaseHubDbHelper(MainActivity.c);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] projection = {
				FavoriteStopEntry._ID,
				FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG
		};
		String sortOrder = FavoriteStopEntry._ID;
		String selection = FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG + " LIKE ?";
		String[] selectionArgs = {stop.getTag()};
		Cursor c = db.query(
				FavoriteStopEntry.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder
				);
		c.moveToFirst();
		if (c.getCount() > 0){
			c.close();
			return true;	
		}else{
			c.close();
			return false;
		}
	}

	@Override
	protected Boolean doInBackground(Stop... params) {
		return isFavoriteStop(params[0]);
	}

}
