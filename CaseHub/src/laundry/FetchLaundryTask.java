package laundry;

import java.io.IOException;
import java.util.ArrayList;

import laundry.LaundryFragment.LaundryCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import schedule.ScheduleEvent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Fetches laundry information from http://case-asi.esuds.net/
 */
public class FetchLaundryTask extends AsyncTask<String, Void, String> {
	
	Context context;
	LaundryCallback callback;
	DefaultHttpClient client;
	ProgressDialog dialog;
	
	private static final String ESUDS_URL = "http://case-asi.esuds.net/RoomStatus/showRoomStatus.do";
	
	public FetchLaundryTask(Context context, LaundryCallback callback) {
		this.context = context;
		this.callback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		
		client = new DefaultHttpClient();
		String result = "";
		
		try {
			result = getResidenceHalls();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
        callback.onTaskDone();        
        dialog.dismiss();
	}
	
	/*
	 * Retrieves list of residence halls
	*/ 
	private String getResidenceHalls() throws ClientProtocolException, IOException {
		
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    // GET login form
		HttpGet get = new HttpGet(ESUDS_URL);
		HttpResponse loginGetResult = client.execute(get);
		HttpEntity entity = loginGetResult.getEntity();
		
		return EntityUtils.toString(entity, "UTF-8");
	}
	
	/*
	private String getLaundryTimes(int houseID) {
		
	}
	*/
	
}