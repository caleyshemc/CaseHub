package schedule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

/*
 * Handles connection, login, and scraping of the Case Single Sign-On sites.
 * TODO: Determine best I/O types for AsyncTask
 */
public class CaseSSOConnector extends AsyncTask<String, String, String> {

	//private String destination;
	
	// TODO: define in file
	private static final String SSO_URL = "https://login.case.edu/cas/login?service=";
	// https://login.case.edu/cas/login?service=https%3a%2f%2fm.case.edu%2fgadget_s.html%3f_gid%3dmyschedule
	
	/**
	 * 
	 * @param user
	 * @param password
	 * @throws IOException
	 */
	private String login(String user, String password) throws IOException {
		
		String result = "Login failed in login().";
		
		URL url = new URL(SSO_URL);
		
		// Specify POST request
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		/*
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		*/
		urlConnection.setRequestMethod("GET");
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(2000000);
		
		java.util.Scanner s = null;
		
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			s = new java.util.Scanner(in).useDelimiter("\\A");
		    result = s.hasNext() ? s.next() : "";
		}
		finally {
			urlConnection.disconnect();
		}
		
		return result;
		
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
