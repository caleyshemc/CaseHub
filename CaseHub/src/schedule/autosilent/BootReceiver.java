package schedule.autosilent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * BootReceiver automatically (re)starts the autosilent receivers when the
 * device is rebooted.
 * 
 * It is enabled/disabled in SilenceReceiver's schedule() and cancel() methods.
 */
public class BootReceiver extends BroadcastReceiver {
	
    SilenceReceiver silenceReceiver = new SilenceReceiver();
    UnsilenceReceiver unsilenceReceiver = new UnsilenceReceiver();
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            silenceReceiver.schedule(context);
            unsilenceReceiver.schedule(context);
        }
        
    }
    
}
