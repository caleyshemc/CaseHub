package schedule.autosilent;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.LocalTime;

import schedule.ScheduleDBHelper;
import schedule.ScheduleFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.util.Log;

public class SilenceReceiver extends BroadcastReceiver {
		
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	
    	SharedPreferences settings = context.getSharedPreferences(ScheduleFragment.SILENT_PREF, 0);
		int autoSilentSetting = settings.getInt(ScheduleFragment.SILENT, ScheduleFragment.SILENT_OFF);
    	
    	// Set ringer to silent or vibrate
    	switch (autoSilentSetting) {
    	case ScheduleFragment.SILENT_OFF:
    		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    		break;
    	case ScheduleFragment.SILENT_VIBRATE:
    		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    		break;
    	default:
    		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    		Log.w("CASEHUB","SilenceReceiver did not retrieve autosilent preferences correctly.");
    		break;
    	}
    	
    	
    }
    
	/**
	 * Schedules phone to go on silent or vibrate at the beginning of each
	 * class.
	 * 
	 * Also enables BootReceiver so rec is set on device startup.
	 */
    public void schedule(Context context) {
    	    	
    	alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SilenceReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        
        /* Schedule broadcasts */
        ScheduleDBHelper dbHelper = new ScheduleDBHelper();
        ArrayList<LocalTime> startTimes = dbHelper.getStartTimes();
        
        if (startTimes.isEmpty()) {
        	Log.w("CASEHUB", "Attempted to schedule AutoSilent events for empty schedule.");
        	return;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        
        for (LocalTime time : startTimes) {
        	
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
     * 
     * Also disables BootReceiver so it is not called on device startup.
     */
    public void cancel(Context context) {
    	    	
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
            	
    	// Disable BootReceiver so that it doesn't automatically restart the 
        // alarm on device startup.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    
}