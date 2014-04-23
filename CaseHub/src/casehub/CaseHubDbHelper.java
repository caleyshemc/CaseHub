package casehub;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Defines tables, relationships, and operations for the SQLite database.
 */
public class CaseHubDbHelper extends SQLiteOpenHelper {

    /**
     * If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "CaseHub.db";

    public CaseHubDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CaseHubContract.SQL_CREATE_SCHEDULE_ENTRIES);
        db.execSQL(CaseHubContract.SQL_CREATE_LAUNDRY_ENTRIES);
        db.execSQL(CaseHubContract.SQL_CREATE_GREENIE_ENTRIES);
        db.execSQL(CaseHubContract.SQL_CREATE_MAP_POINTS);
        db.execSQL(CaseHubContract.SQL_CREATE_MAP_SUBGROUPS);
        db.execSQL(CaseHubContract.SQL_CREATE_MAP_TYPES);
    }
    
    /**
	 * Called when upgrading DATABASE_VERSION. Depending on the change, this
	 * method can be written to drop and recreate tables, or just add/change
	 * columns.
	 * 
	 * Currently, it simply drops and recreates the database.
	 */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CaseHubContract.SQL_DELETE_SCHEDULE_ENTRIES);
        db.execSQL(CaseHubContract.SQL_DELETE_LAUNDRY_ENTRIES);
        db.execSQL(CaseHubContract.SQL_DELETE_GREENIE_ENTRIES);
        db.execSQL(CaseHubContract.SQL_DELETE_MAP_POINTS);
        db.execSQL(CaseHubContract.SQL_DELETE_MAP_SUBGROUPS);
        db.execSQL(CaseHubContract.SQL_DELETE_MAP_TYPES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}