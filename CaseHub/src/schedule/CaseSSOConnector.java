package schedule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * Handles connection, login, and scraping of the Case Single Sign-On sites.
 */
public class CaseSSOConnector {

	private String destination;
	
	// TODO: define in file
	private static final String SSO_URL = "https://login.case.edu/cas/login?service=";
	// https://login.case.edu/cas/login?service=https%3a%2f%2fm.case.edu%2fgadget_s.html%3f_gid%3dmyschedule
	
	public CaseSSOConnector(String destination) {
		this.destination = destination;
	}
	
	/**
	 * 
	 * @param user
	 * @param password
	 * @throws IOException
	 */
	public void login(String user, String password) throws IOException {
		
		URL url = new URL(SSO_URL + destination);
		
		// Specify POST request
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			//readStream(in);
		}
		finally {
			urlConnection.disconnect();
		}
	}
	
}
