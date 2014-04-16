package laundry;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

/**
 * Fetches laundry information from http://case-asi.esuds.net/
 */
public class FetchLaundryTask extends AsyncTask<String, Void, String> {
	
	private static final String ESUDS_URL = "http://case-asi.esuds.net/RoomStatus/showRoomStatus.do";

	DefaultHttpClient client;

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