package schedule.autosilent;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.LocalTime;

import schedule.ScheduleDBHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;

public class UnsilenceReceiver extends BroadcastReceiver {
		
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {
    	AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
    
    /**
     * Schedules phone to go off silent/vibrate at the end of each class.
     */
    public void schedule(Context context) {
    	
    	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UnsilenceReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		/* Schedule broadcasts */
        ScheduleDBHelper dbHelper = new ScheduleDBHelper();
        ArrayList<LocalTime> endTimes = dbHelper.getEndTimes();
        
        if (endTimes.isEmpty()) {
        	Log.w("CASEHUB", "Attempted to schedule AutoSilent events for empty schedule.");
        	return;
        }

        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        
        for (LocalTime time : endTimes) {
        	
        	// Set daily trigger for this time
            calendar.set(Calendar.HOUR_OF_DAY, time.getHourOfDay());
            calendar.set(Calendar.MINUTE, time.getMinuteOfHour());
    		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
    				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
    				alarmIntent);
        }
    	
		// Enable BootReceiver to automatically restart the alarm on device
		// startup.
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    
    /**
     * If the alarm has been set, cancel it.
     */
    public void cancel() {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
    
}