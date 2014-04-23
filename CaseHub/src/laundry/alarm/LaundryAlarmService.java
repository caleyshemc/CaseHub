package laundry.alarm;

import java.util.ArrayList;

import com.casehub.R;

import laundry.FetchLaundryTask;
import laundry.LaundryMachine;
import laundry.LaundryFragment.LaundryCallback;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LaundryAlarmService extends IntentService {
	
	public static String HOUSE_ID = "houseId";
    public static String MACHINE_NUM = "machineNum";
    public static String STATUS = "status";
    public static String TYPE = "type";
    
    Context context;
    Bundle args;
    
	public LaundryAlarmService() {
		super("LaundryAlarmService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		context = getApplicationContext();
		args = intent.getExtras();

		Log.d("LAUNDRY", "Extras: " + args.toString());
		
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

		for (LaundryMachine machine : machines) {

			if (machine.getMachineNumber() == num) {

				String status = machine.getStatus().getString();

				if (!status.equals(args.getString(STATUS))) {
	
					NotificationManager mNM = (NotificationManager) context
							.getSystemService(context.NOTIFICATION_SERVICE);
	
					// Create notification
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle(
									"CaseHub: Laundry machine status changed")
							.setContentText(type + " " + num + ": " + status);
	
					Notification notification = mBuilder.build();
	
					mNM.notify(0, notification);
	
					// TODO Cancel alarm
					//this.cancel(context);

				}

			}

		}

	}
	
	private void cancelAlarm() {
		
	}

}
