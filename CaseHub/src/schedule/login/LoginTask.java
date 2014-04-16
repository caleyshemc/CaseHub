package schedule.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

import schedule.Day;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Handles connection, login, and scraping of the Case Single Sign-On sites.
 */
public class LoginTask extends AsyncTask<String, Void, String> {
	
	DefaultHttpClient client;
	
	List<Exception> exceptions = new ArrayList<Exception>();
		
	private static final String SSO_URL = "https://login.case.edu/cas/login";
	private static final String SCHEDULE_URL = "http://scheduler.case.edu";
	
	@Override
	protected String doInBackground(String... args) {
		
		client = new DefaultHttpClient();
		
		String result = "";
		
		// TODO check args!
		
		try {
			login(args[0], args[1]);
			result = getSchedule();
		} catch (IOException e) {
			exceptions.add(e);
		}
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
        
        for (Exception e : exceptions) {
        	// TODO inform login dialog that login failed
        	Log.e("CASEHUB", "exception", e);
        }
        
	}
	
	/**
	 * Logs in to Case's Single Sign-On and then loads the page specified in url
	 * @throws IOException
	 */
	private void login(String user, String password) throws IOException {
				
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    // GET login form
		HttpGet loginGet = new HttpGet(SSO_URL);
		HttpResponse loginGetResult = client.execute(loginGet);
		HttpEntity entity = loginGetResult.getEntity();
		
		// Parse HTML to find login ticket
		String responseString = EntityUtils.toString(entity, "UTF-8");
		Document doc = Jsoup.parse(responseString);
		Elements input = doc.getElementsByAttributeValue("name", "lt");
		String login_ticket = input.attr("value");
		loginGetResult.getEntity().consumeContent();

		// Prepare POST to log in to SSO
		HttpPost loginPost = new HttpPost(SSO_URL);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("lt", login_ticket));
		nameValuePairs.add(new BasicNameValuePair("username", user));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		loginPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Execute login POST
		HttpResponse postResult = client.execute(loginPost);
		postResult.getEntity().consumeContent();
		
		// TODO return boolean to indicate successful login!
	}

	private String getSchedule() throws IOException {
		
		String resultString = "";
		
		// GET Scheduler to set appropriate cookies
		HttpGet schedGet = new HttpGet(SCHEDULE_URL);
		HttpResponse getSchedResult = client.execute(schedGet);
		getSchedResult.getEntity().consumeContent();	
		
		for (Day day : Day.values()) {
			
			String url = SCHEDULE_URL + "/day.php?day=" + day.getCode();
			
			// GET schedule events for each day of the week
			HttpGet dayGet = new HttpGet(url);
			HttpResponse getDayResult = client.execute(dayGet);
			HttpEntity dayEntity = getDayResult.getEntity();
						
			// Wrap each day in a div for easy parsing
			resultString += "<div id='" + day.toString() + "'>";
			resultString += EntityUtils.toString(dayEntity, "UTF-8");
			resultString += "</div>";
			
			getDayResult.getEntity().consumeContent();
		}
		
		return resultString;
		
	}
	
}
