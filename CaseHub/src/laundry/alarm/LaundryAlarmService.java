package laundry.alarm;

import java.util.ArrayList;

import laundry.FetchLaundryTask;
import laundry.LaundryFragment.LaundryCallback;
import laundry.LaundryMachine;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.casehub.R;

public class LaundryAlarmService extends IntentService {
	
	public static String HOUSE_ID = "houseId";
    public static String MACHINE_NUM = "machineNum";
    public static String STATUS = "status";
    public static String TYPE = "type";
    
    public static String INTENT_KEY = "intentKey";
    
    Context context;
    Bundle args;
    
    int intentKey;
    
	public LaundryAlarmService() {
		super("LaundryAlarmService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		context = getApplicationContext();
		args = intent.getExtras();
		intentKey = args.getInt(INTENT_KEY);
		
		// Check machine for status change
		new FetchLaundryTask(context, new LaundryCallback() {

			@Override
			public void onTaskDone(ArrayList<LaundryMachine> machines) {
				onFetchLaundryTaskDone(machines);
			}
		}, args.getInt(HOUSE_ID), false).execute();

	}

	private void onFetchLaundryTaskDone(ArrayList<LaundryMachine> machines) {

		int num = args.getInt(MACHINE_NUM);
		String type = args.getString(TYPE);
		String status;

		for (LaundryMachine machine : machines) {

			status = machine.getStatus().getString();

			// If machine status changed
			if (machine.getMachineNumber() == num
					&& !status.equals(args.getString(STATUS))) {

				notifyUser(type, num, status);
				cancelAlarm();

			}

		}

	}
	
	/*
	 * Notify user of laundry machine status change
	 */
	private void notifyUser(String type, int num, String status) {
		
		// Create notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(
						"CaseHub: Laundry machine status changed")
				.setContentText(type + " " + num + ": " + status)
				.setAutoCancel(true);
		Notification notification = mBuilder.build();
		
		// Display notification
		NotificationManager mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNM.notify(0, notification);
		
		// Play alarm sound
		Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), tone);
		r.play();
		
	}
	
	private void cancelAlarm() {
		
		PendingIntent intent = LaundryAlarmReceiver.intentMap.get(intentKey);
		
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(intent);
		
	}

}
