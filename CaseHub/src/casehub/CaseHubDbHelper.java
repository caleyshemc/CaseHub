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
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "CaseHub.db";

    public CaseHubDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CaseHubContract.SQL_CREATE_ENTRIES);
    }
    
    /**
	 * Called when upgrading DATABASE_VERSION. Depending on the change, this
	 * method can be written to drop and recreate tables, or just add/change
	 * columns.
	 * 
	 * Currently, it simply drops and recreates the database.
	 */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CaseHubContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}