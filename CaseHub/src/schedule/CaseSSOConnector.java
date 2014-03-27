package schedule;

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

import android.os.AsyncTask;

/**
 * Singleton class.
 * Handles connection, login, and scraping of the Case Single Sign-On sites.
 * TODO: Determine best I/O types for AsyncTask
 */
public class CaseSSOConnector extends AsyncTask<String, Void, String> {

	private static CaseSSOConnector instance = null;
	
	private String cookies;
	private DefaultHttpClient client = new DefaultHttpClient();
	private final String USER_AGENT = "Mozilla/5.0";

	//private String destination;
	
	// TODO: define in file
	private static final String SSO_URL = "https://login.case.edu/cas/login?service=";
	// https://login.case.edu/cas/login?service=https%3a%2f%2fm.case.edu%2fgadget_s.html%3f_gid%3dmyschedule
	
	
	protected CaseSSOConnector() {
		// Exists to defeat instantiation as part of the Singleton pattern.
	}

	/**
	 * Returns singleton instance.
	 */
	public static CaseSSOConnector getInstance() {
		if (instance == null) {
			instance = new CaseSSOConnector();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param user
	 * @param password
	 * @throws IOException
	 */
	private String login(String user, String password) throws IOException {
		
		String response = "Login failed in login().";

		/*TODO TEST*/
		user = "crs133";
		password = "TEST";
		String url = "https://login.case.edu/cas/login";
		
		BasicCookieStore cookieStore = new BasicCookieStore();
	    client.setCookieStore(cookieStore);

	    			

		HttpGet httpGet = new HttpGet(url);
		HttpResponse result = client.execute(httpGet);

		HttpEntity entity = result.getEntity();
		String responseString = EntityUtils.toString(entity, "UTF-8");

		Document doc = Jsoup.parse(responseString);
		Elements input = doc.getElementsByAttributeValue("name", "lt");
		String login_ticket = input.attr("value");

		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("lt", login_ticket));
		nameValuePairs.add(new BasicNameValuePair("username", user));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		result = client.execute(httpPost);

		entity = result.getEntity();
		response = EntityUtils.toString(entity, "UTF-8");

		
		httpGet = new HttpGet("https://m.case.edu/gadget_s.html?_gid=myschedule");
		//params.setParameter(ClientPNames.COOKIE_POLICY, "easy");
		
		result = client.execute(httpGet);
		entity = result.getEntity();
		response = EntityUtils.toString(entity, "UTF-8");
		
		if (response.contains("successfully logged in")) {
			return "successful login!";
		}
		    
		return response;
		
	}

	@Override
	protected String doInBackground(String... args) {
		String loginResult = "Login failed in doInBackground().";
		try {
			loginResult = login(args[0], args[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginResult;
	}
	
}
