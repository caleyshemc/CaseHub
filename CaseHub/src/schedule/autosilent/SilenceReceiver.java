package schedule.autosilent;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;

public class SilenceReceiver extends BroadcastReceiver {
	
	// TODO check option for silent vs vibrate
	
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
  
    @Override
    public void onReceive(Context context, Intent intent) {   
    	AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    	audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    
	/**
	 * Schedules phone to go on silent or vibrate at the beginning of each
	 * class.
	 * 
	 * Also enables BootReceiver so rec is set on device startup.
	 */
    public void schedule(Context context) {
    	    	
    	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SilenceReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // TODO determine whether to schedule silent or vibrate
        
        
        /*
         * 
         * TODO: test
         * 
         
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				alarmIntent);
		*/
		
		
    	
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
        
        // TODO call unsilence cancel()?
    	
    	// Disable BootReceiver so that it doesn't automatically restart the 
        // alarm on device startup.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    
}