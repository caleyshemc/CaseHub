package casehub;

import android.provider.BaseColumns;

public final class CaseHubContract {
	
    /** 
     * Empty constructor to prevent accidental instantiation of the contract class.
     */
    public CaseHubContract() {}

    /**
     *  Defines schedule event table. 
     */
    public static abstract class ScheduleEventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_EVENT_NAME = "name";
        public static final String COLUMN_NAME_EVENT_LOCATION = "location";
        public static final String COLUMN_NAME_EVENT_START = "start";
        public static final String COLUMN_NAME_EVENT_END = "end";
        public static final String COLUMN_NAME_EVENT_DAY = "day";
    }
    
    
    /*
     * Database creation/maintenance methods
     */
    private static final String INT_TYPE = " INT";
    private static final String STRING_TYPE = " STRING";
    private static final String COMMA_SEP = ",";
    
    /*
     * Define tables and options here
     */
    protected static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + ScheduleEventEntry.TABLE_NAME + " (" +
        ScheduleEventEntry._ID + " INTEGER PRIMARY KEY," +
        ScheduleEventEntry.COLUMN_NAME_EVENT_ID + INT_TYPE + COMMA_SEP +		
        ScheduleEventEntry.COLUMN_NAME_EVENT_NAME + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COLUMN_NAME_EVENT_LOCATION + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COLUMN_NAME_EVENT_START + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COLUMN_NAME_EVENT_END + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COLUMN_NAME_EVENT_DAY + STRING_TYPE +   
        " );";

    protected static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + ScheduleEventEntry.TABLE_NAME;
        
}