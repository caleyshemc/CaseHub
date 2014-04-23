package laundry.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LaundryAlarmReceiver extends BroadcastReceiver {

	// The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    
    public static long INTERVAL_SECONDS = 60;
    
    Context context;
	
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
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 
        		INTERVAL_SECONDS * 1000, alarmIntent);
		
	}
	
	public void cancel(Context context) {
		// TODO
		if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
	}
	
}
