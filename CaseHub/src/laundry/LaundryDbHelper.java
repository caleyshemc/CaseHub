package laundry;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import casehub.CaseHubContract.LaundryHouseEntry;
import casehub.MainActivity;

/**
 * Saves and retrieves laundry information from database.
 */
public class LaundryDbHelper {

	/**
	 * Adds houses (residence calls) and their IDs to the database.
	 */
	public void addHouses(HashMap<String, Integer> houses) {

		for (Map.Entry<String, Integer> house : houses.entrySet()) {
			
			// Create map of values
			ContentValues values = new ContentValues();
			values.put(LaundryHouseEntry.COL_HOUSE_NAME, house.getKey());
			values.put(LaundryHouseEntry.COL_HOUSE_ID, house.getValue());

			// Insert values into database
			SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
			db.insert(LaundryHouseEntry.TABLE_NAME, null, values);
			
		}
		
	}

	/**
	 * Returns list of houses in the database.
	 */
	public HashMap<String, Integer> getHouses() {

		// Retrieve database
		SQLiteDatabase db = MainActivity.mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns to retrieve
		String[] projection = { LaundryHouseEntry.COL_HOUSE_NAME,
				LaundryHouseEntry.COL_HOUSE_ID };

		Cursor c = db.query(LaundryHouseEntry.TABLE_NAME, // The table to query
				projection, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		HashMap<String, Integer> houses = new HashMap<String, Integer>();

		String name;
		int id;

		// Grab column indices
		int name_index = c.getColumnIndexOrThrow(LaundryHouseEntry.COL_HOUSE_NAME);
		int id_index = c.getColumnIndexOrThrow(LaundryHouseEntry.COL_HOUSE_ID);

		// If no entries found
		if (!c.moveToFirst()) {
			// TODO throw error
			return houses;
		}

		do {

			name = c.getString(name_index);
			id = c.getInt(id_index);

			houses.put(name, id);

		} while (c.moveToNext());

		return houses;
	}

	/**
	 * Deletes all houses from the database.
	 */
	public void clearHouses() {
		SQLiteDatabase db = MainActivity.mDbHelper.getWritableDatabase();
		db.delete(LaundryHouseEntry.TABLE_NAME, null, null);
	}
	
}
