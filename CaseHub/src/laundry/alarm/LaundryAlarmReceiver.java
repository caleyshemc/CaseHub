package laundry.alarm;

import java.util.ArrayList;

import laundry.FetchLaundryTask;
import laundry.LaundryFragment.LaundryCallback;
import laundry.LaundryMachine;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class LaundryAlarmReceiver extends BroadcastReceiver {

	// The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    
    public static long INTERVAL_SECONDS = 60;
    
    public static String HOUSE_ID = "houseId";
    public static String MACHINE_NUM = "machineNum";
    public static String STATUS = "status";
    
    Bundle args;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		args = intent.getExtras();
		
		// Check machine for status change
		new FetchLaundryTask(context, new LaundryCallback() {
			
			@Override
			public void onTaskDone(ArrayList<LaundryMachine> machines) {
				onFetchLaundryTaskDone(machines);
			}
		}, args.getInt(HOUSE_ID)).execute();
		
	}
	
	private void onFetchLaundryTaskDone(ArrayList<LaundryMachine> machines) {
		
		for (LaundryMachine machine : machines) {
			
			if (machine.getMachineNumber() == args.getInt(MACHINE_NUM)) {
				
				String status = machine.getStatus().getString();
				
				if (!status.equals(args.getString(STATUS))) {
					// TODO alert user
				}
				
			}
			
		}
		
	}
	
	/**
	 * Set alarm alerting user to change in machine status.
	 */
	public void setAlarm(Context context, int houseId, int machineNum, String status) {
		
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LaundryAlarmReceiver.class);
        intent.putExtra(HOUSE_ID, houseId);
        intent.putExtra(MACHINE_NUM, machineNum);
        intent.putExtra(STATUS, status);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 
        		INTERVAL_SECONDS * 1000, alarmIntent);
		
	}

	/* TODO
	 * -- Dialog asks if you shoud dismiss or keep alerting
	 */
	
	public void cancel(Context context) {
		// TODO
	}
	
}
