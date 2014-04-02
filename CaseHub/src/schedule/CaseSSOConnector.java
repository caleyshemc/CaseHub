package schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Singleton class.
 * Handles connection, login, and scraping of the Case Single Sign-On sites.
 * TODO: Determine best I/O types for AsyncTask
 */
public class CaseSSOConnector extends AsyncTask<String, Void, String> {

	private static boolean loggedIn = false;
	private static DefaultHttpClient client = new DefaultHttpClient();	
	
	private static final String SSO_URL = "https://login.case.edu/cas/login";
	private static final String SCHEDULE_URL = "http://scheduler.case.edu";

	
	/**
	 * Logs in to Case's Single Sign-On and then loads the page specified in url
	 * @throws IOException
	 */
	private String login(String user, String password) throws IOException {
				
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    // GET login form
		HttpGet httpGet = new HttpGet(SSO_URL);
		HttpResponse result = client.execute(httpGet);
		HttpEntity entity = result.getEntity();
		
		// Parse HTML to find login ticket
		String responseString = EntityUtils.toString(entity, "UTF-8");
		Document doc = Jsoup.parse(responseString);
		Elements input = doc.getElementsByAttributeValue("name", "lt");
		String login_ticket = input.attr("value");

		// Prepare POST to log in to SSO
		HttpPost httpPost = new HttpPost(SSO_URL);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("lt", login_ticket));
		nameValuePairs.add(new BasicNameValuePair("username", user));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute login POST
		result = client.execute(httpPost);
		
		return "der"; // TODO return boolean
	}

	private String getSchedule() throws ClientProtocolException, IOException {
		
		String resultString = "";
		String[] weekdays = {"M", "T", "W", "R", "F"};
		
		// GET Scheduler to set appropriate cookies
		HttpGet httpGet = new HttpGet(SCHEDULE_URL);
		HttpResponse result = client.execute(httpGet);
		HttpEntity entity = result.getEntity();
		
		for (String day : weekdays) {
			
			String url = SCHEDULE_URL + "/day.php?day=" + day;
			
			// GET schedule events for each day of the week
			httpGet = new HttpGet(url);
			result = client.execute(httpGet);
			entity = result.getEntity();
			
			// Wrap each day in a div for easy parsing
			resultString += "<div id='" + day + "'>";
			resultString += EntityUtils.toString(entity, "UTF-8");
			resultString += "</div>";
		}
		
		return resultString;
		
	}
	
	@Override
	protected String doInBackground(String... args) {
		String loginResult = "Login failed in doInBackground().";
		try {
			if (!loggedIn) {
				loginResult = login(args[0], args[1]);
				loggedIn = true; // TODO check HTTP response!
			}
			loginResult = getSchedule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginResult;
	}
	
}
