package laundry.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

public class LaundryAlarmReceiver extends BroadcastReceiver {

	// The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    
    public static SparseArray<PendingIntent> intentMap;
    
    public static Integer key_count = 1;
    public Integer intentKey;
    
    public static final long INTERVAL_SECONDS = 60;
    
    Context context;

    public LaundryAlarmReceiver() {
    	
    	if (intentMap == null) {
        	intentMap = new SparseArray<PendingIntent>();
        }
    	
        intentKey = key_count++;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		Intent serviceIntent = new Intent(context, LaundryAlarmService.class);
		serviceIntent.putExtras(intent);
				
		// Start service to check for status changes and notify user
		context.startService(serviceIntent);
		
	}
	
	/**
	 * Set alarm alerting user to change in machine status.
	 */
	public void setAlarm(Context context, int houseId, int machineNum, String status, String type) {
		
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LaundryAlarmReceiver.class);
        intent.putExtra(LaundryAlarmService.HOUSE_ID, houseId);
        intent.putExtra(LaundryAlarmService.MACHINE_NUM, machineNum);
        intent.putExtra(LaundryAlarmService.STATUS, status);
        intent.putExtra(LaundryAlarmService.TYPE, type);
        intent.putExtra(LaundryAlarmService.INTENT_KEY, intentKey);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Store key to keep track of alarm intent
        intentMap.put(intentKey, alarmIntent);
        
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 
        		INTERVAL_SECONDS * 1000, alarmIntent);
		
	}
	
}
