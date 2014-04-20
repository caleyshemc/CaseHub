package laundry;

import java.io.IOException;
import java.util.HashMap;

import laundry.LaundryFragment.LaundryHousesCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Fetches residence hall list from Case eSuds.
 */
public class FetchHousesTask extends AsyncTask<String, Void, HashMap<String, Integer>> {
	
	Context context;
	LaundryHousesCallback callback;
	DefaultHttpClient client;
	ProgressDialog dialog;

	private static final String ESUDS_URL = "http://case-asi.esuds.net/RoomStatus/showRoomStatus.do";
	private static final String HOUSE_SELECTOR = "span.dormlinks a";
	private static final String HOUSE_LIST_SELECTOR = "#menu";
	private static final String HOUSE_ID_ATTR = "href";
	
	public FetchHousesTask(Context context, LaundryHousesCallback callback) {
		this.context = context;
		this.callback = callback;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.setMessage("Fetching residence halls...");
		dialog.show();
	}
	
	@Override
	protected HashMap<String, Integer> doInBackground(String... params) {
		
		client = new DefaultHttpClient();
		HashMap<String, Integer> houses = new HashMap<String, Integer>();
		
		try {
			String html = getResidenceHalls();
			houses = parseResidenceHalls(html);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return houses;
	}
	
	@Override
	protected void onPostExecute(HashMap<String, Integer> houses) {
		super.onPostExecute(houses);
        callback.onTaskDone(houses);        
        dialog.dismiss();
	}
	
	/*
	 * Retrieves HTML to be parsed for list of residence halls.
	 */ 
	private String getResidenceHalls() throws ClientProtocolException, IOException {
		
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    // GET page
		HttpGet get = new HttpGet(ESUDS_URL);
		HttpResponse loginGetResult = client.execute(get);
		HttpEntity entity = loginGetResult.getEntity();
		
		return EntityUtils.toString(entity, "UTF-8");
	}
	
	private HashMap<String, Integer> parseResidenceHalls(String html) {
		
		HashMap<String, Integer> houses = new HashMap<String, Integer>();
		Document doc = Jsoup.parse(html);
		
		Element houseList = doc.select(HOUSE_LIST_SELECTOR).first();
		Elements houseElements = houseList.select(HOUSE_SELECTOR);
		
		String houseName;
		int houseId;
		for (Element house : houseElements) {
			
			houseName = house.text();
			houseId = Integer.parseInt(house.attr(HOUSE_ID_ATTR).replaceAll("\\D+",""));
			
			houses.put(houseName, houseId);
		}
		
		return houses;
	}
	
}
