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
        public static final String COL_EVENT_ID = "event_id";
        public static final String COL_EVENT_NAME = "name";
        public static final String COL_EVENT_LOCATION = "location";
        public static final String COL_EVENT_START = "start";
        public static final String COL_EVENT_END = "end";
        public static final String COL_EVENT_DAY = "day";
    }
    
    public static abstract class LaundryHouseEntry implements BaseColumns {
    	public static final String TABLE_NAME = "laundry_house";
    	public static final String COL_HOUSE_NAME = "house_name";
    	public static final String COL_HOUSE_ID = "house_id";
    }
    
    public static abstract class FavoriteStopEntry implements BaseColumns {
    	public static final String TABLE_NAME = "favorite_stops";
    	public static final String COLUMN_NAME_FAVORITE_STOP_TAG = "favorite_stop_tag";
    }
    
  //Defines Campus Map tables
  	public static abstract class CampusMapPoint implements BaseColumns{
  		public static final String TABLE_NAME = "campus_map_points";
  		public static final String COL_NUM = "num";
  		public static final String COL_NAME = "name";
  		public static final String COL_ADDRESS = "address";
  		public static final String COL_LAT = "lat";
  		public static final String COL_LNG = "lng";
  		public static final String COL_SIS = "sis";
  		public static final String COL_IMAGE = "image";
  		public static final String COL_URL = "url";
  		public static final String COL_TYPE_ID = "type_id";
  		public static final String COL_ZONE = "zone";
  		public static final String COL_LDAP = "ldap";
  		public static final String COL_EXTRA_NAMES = "extra_names";
  		public static final String COL_ENTITIES = "entities";
  	}

  	public static abstract class CampusMapSubgroup implements BaseColumns{
  		public static final String TABLE_NAME = "campus_map_subgroups";
  		public static final String COL_ID = "id";
  		public static final String COL_NAME = "name";
  		public static final String COL_LAT = "lat";
  		public static final String COL_LNG = "lng";
  		public static final String COL_TYPE_ID = "type_id";
  	}

  	public static abstract class CampusMapType implements BaseColumns{
  		public static final String TABLE_NAME = "campus_map_types";
  		public static final String COL_COLOR = "color";
  		public static final String COL_NAME = "name";
  		public static final String COL_TYPE_ID = "type_id";
  	}    
    
    /*
     * Database creation/maintenance methods
     */
    private static final String INT_TYPE = " INT";
    private static final String REAL_TYPE = " REAL";
    private static final String STRING_TYPE = " STRING";
    private static final String COMMA_SEP = ",";
    
    /*
     * Define tables and options here
     */
    protected static final String SQL_CREATE_SCHEDULE_ENTRIES =
        "CREATE TABLE " + ScheduleEventEntry.TABLE_NAME + " (" +
        ScheduleEventEntry._ID + " INTEGER PRIMARY KEY," +
        ScheduleEventEntry.COL_EVENT_ID + INT_TYPE + COMMA_SEP +		
        ScheduleEventEntry.COL_EVENT_NAME + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COL_EVENT_LOCATION + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COL_EVENT_START + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COL_EVENT_END + STRING_TYPE + COMMA_SEP +
        ScheduleEventEntry.COL_EVENT_DAY + STRING_TYPE +   
        " );";
    
    protected static final String SQL_CREATE_LAUNDRY_ENTRIES =
            "CREATE TABLE " + LaundryHouseEntry.TABLE_NAME + " (" +
            LaundryHouseEntry._ID + " INTEGER PRIMARY KEY," +
            LaundryHouseEntry.COL_HOUSE_NAME + STRING_TYPE + COMMA_SEP +		
            LaundryHouseEntry.COL_HOUSE_ID + INT_TYPE +  
            " );";
    
    protected static final String SQL_CREATE_GREENIE_ENTRIES =
        	"CREATE TABLE " + FavoriteStopEntry.TABLE_NAME + " (" +
        	FavoriteStopEntry._ID + " INTEGER PRIMARY KEY," +
        	FavoriteStopEntry.COLUMN_NAME_FAVORITE_STOP_TAG + STRING_TYPE + " );";
    
    protected static final String SQL_CREATE_MAP_POINTS =
			"CREATE TABLE " + CampusMapPoint.TABLE_NAME + " (" +
					CampusMapPoint._ID + " INTEGER PRIMARY KEY," +
					CampusMapPoint.COL_NUM + STRING_TYPE + COMMA_SEP +		
					CampusMapPoint.COL_NAME + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_ADDRESS + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_LAT + REAL_TYPE + COMMA_SEP +
					CampusMapPoint.COL_LNG + REAL_TYPE + COMMA_SEP +
					CampusMapPoint.COL_SIS + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_IMAGE + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_URL + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_TYPE_ID + INT_TYPE + COMMA_SEP +
					CampusMapPoint.COL_ZONE + INT_TYPE + COMMA_SEP +
					CampusMapPoint.COL_LDAP + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_EXTRA_NAMES + STRING_TYPE + COMMA_SEP +
					CampusMapPoint.COL_ENTITIES + STRING_TYPE +
					" );";

	protected static final String SQL_CREATE_MAP_SUBGROUPS =
			"CREATE TABLE " + CampusMapSubgroup.TABLE_NAME + " (" +
					CampusMapSubgroup._ID + " INTEGER PRIMARY KEY," +
					CampusMapSubgroup.COL_ID + STRING_TYPE + COMMA_SEP +		
					CampusMapSubgroup.COL_NAME + STRING_TYPE + COMMA_SEP +
					CampusMapSubgroup.COL_LAT + REAL_TYPE + COMMA_SEP +
					CampusMapSubgroup.COL_LNG + REAL_TYPE + COMMA_SEP +
					CampusMapSubgroup.COL_TYPE_ID + INT_TYPE +
					" );";
	
	protected static final String SQL_CREATE_MAP_TYPES =
			"CREATE TABLE " + CampusMapType.TABLE_NAME + " (" +
					CampusMapType._ID + " INTEGER PRIMARY KEY," +
					CampusMapType.COL_COLOR + STRING_TYPE + COMMA_SEP +		
					CampusMapType.COL_NAME + STRING_TYPE + COMMA_SEP +
					CampusMapType.COL_TYPE_ID + INT_TYPE +
					" );";
    
    protected static final String SQL_DELETE_SCHEDULE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScheduleEventEntry.TABLE_NAME;
    
    protected static final String SQL_DELETE_LAUNDRY_ENTRIES =
            "DROP TABLE IF EXISTS " + LaundryHouseEntry.TABLE_NAME;
        
    protected static final String SQL_DELETE_GREENIE_ENTRIES =
            "DROP TABLE IF EXISTS " + FavoriteStopEntry.TABLE_NAME;
    
    protected static final String SQL_DELETE_MAP_POINTS =
			"DROP TABLE IF EXISTS " + CampusMapPoint.TABLE_NAME;
	
	protected static final String SQL_DELETE_MAP_SUBGROUPS =
			"DROP TABLE IF EXISTS " + CampusMapSubgroup.TABLE_NAME;
	
	protected static final String SQL_DELETE_MAP_TYPES =
			"DROP TABLE IF EXISTS " + CampusMapType.TABLE_NAME;
        
}