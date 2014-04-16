package laundry;

import java.io.IOException;
import java.util.ArrayList;

import laundry.LaundryFragment.LaundryCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
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
 * Fetches laundry information from Case eSuds
 * 
 * Call execute() with no third argument for house list,
 * with third argument (int houseId) for laundry times for that house.
 */
public class FetchLaundryTask extends AsyncTask<String, Void, ArrayList<LaundryMachine>> {
	
	Context context;
	LaundryCallback callback;
	DefaultHttpClient client;
	ProgressDialog dialog;
	
	private int houseId;
	
	private static final String ESUDS_HOUSE_URL = "http://case-asi.esuds.net/RoomStatus/showRoomStatus.i?locationId=";
	private static final String STATUS_TABLE_SELECTOR = "#room_status";
	private static final int MACHINE_NUM_INDEX = 1;
	private static final int MACHINE_TYPE_INDEX = 2;
	private static final int MACHINE_STATUS_INDEX = 3;
	private static final int MACHINE_MIN_INDEX = 4;
	
	public FetchLaundryTask(Context context, LaundryCallback callback, int houseId) {
		this.context = context;
		this.callback = callback;
		this.houseId = houseId;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected ArrayList<LaundryMachine> doInBackground(String... params) {
		
		client = new DefaultHttpClient();
		ArrayList<LaundryMachine> machines = new ArrayList<LaundryMachine>();

		try {
			String html = getLaundryTimes();
			machines = parseLaundryTimes(html);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return machines;
	
	}
	
	@Override
	protected void onPostExecute(ArrayList<LaundryMachine> machines) {
		super.onPostExecute(machines);
        callback.onTaskDone(machines);        
        dialog.dismiss();
	}
		
	/*
	 * Retrieves laundry times for a specific house
	 */
	private String getLaundryTimes() throws ParseException, IOException {
		
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    // GET page
		HttpGet get = new HttpGet(ESUDS_HOUSE_URL + houseId);
		HttpResponse loginGetResult = client.execute(get);
		HttpEntity entity = loginGetResult.getEntity();
		
		return EntityUtils.toString(entity, "UTF-8");
	}
	
	private ArrayList<LaundryMachine> parseLaundryTimes(String html) {
		
		ArrayList<LaundryMachine> machines = new ArrayList<LaundryMachine>();
		Document doc = Jsoup.parse(html);
		
		// Get table
		Element statusTable = doc.select(STATUS_TABLE_SELECTOR).first();
		Elements statusTableRows = statusTable.select("tr");
		
		// Extract useful rows
		int machineNumber;
		int minutesLeft;
		String type;
		String status;
		for (Element row : statusTableRows) {
			
			Elements columns = row.select("td");
			if (columns.size() < 4) {
				continue;
			}
			
			machineNumber = Integer.parseInt(columns.get(MACHINE_NUM_INDEX).text());
			minutesLeft = Integer.parseInt(columns.get(MACHINE_MIN_INDEX).text());
			type = columns.get(MACHINE_TYPE_INDEX).text();
			status = columns.get(MACHINE_STATUS_INDEX).text();
			
			LaundryMachine machine = new LaundryMachine(machineNumber, minutesLeft, type, status);
			machines.add(machine);
			
		}
		
		return machines;
	}
	
}