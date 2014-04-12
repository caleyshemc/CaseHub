package schedule.autosilent;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

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
    	/*
    	alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, UnsilenceReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        
         * 
         * TODO: test
         * 
         
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,  
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        */
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