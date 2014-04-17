package dining;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;
import android.text.Html;

public class RSSParseTask extends AsyncTask<String, Void, LinkedHashMap<String, List<String>>> {

	private XmlPullParserFactory xmlFactoryObject;
	LinkedHashMap<String, List<String>> listDataChild;
	List<String> listDataHeader;

	public void fetch(String urlString){
		try{
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();
			xmlFactoryObject = XmlPullParserFactory.newInstance();
			XmlPullParser myparser = xmlFactoryObject.newPullParser();
			myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			myparser.setInput(stream, null);
			parse(myparser);
			stream.close();
		}catch(Exception e){

		}
	}

	private void parse(XmlPullParser xpp) {
		int titleCount = 0;
		listDataChild = new LinkedHashMap<String, List<String>>();
		listDataHeader = new ArrayList<String>();
		ArrayList<String> child = new ArrayList<String>();
		String text = "";
		String description = "";
		try{
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = xpp.getName();
				switch(eventType){
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					break;
				case XmlPullParser.END_TAG:
					if(tagname.equalsIgnoreCase("title")){
						if(titleCount > 0){ //To skip the feed title
							listDataHeader.add(text);
						}
						titleCount++;
					}
					if(tagname.equalsIgnoreCase("description")){
						description = Html.fromHtml(text).toString();
						child.addAll(Arrays.asList(description.split("\n\n")));
						listDataChild.put(listDataHeader.get(titleCount - 2), child);
						child = new ArrayList<String>();
					}
					break;
				case XmlPullParser.TEXT:
					text = xpp.getText();
					break;
				}
				eventType = xpp.next();
			}
		}catch(Exception e){

		}
	}

	@Override
	protected LinkedHashMap<String, List<String>> doInBackground(String... params) {
		fetch(params[0]);
		return listDataChild;
	}

}
