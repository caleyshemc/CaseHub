package greenie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class xmlParse{
	public xmlParse() {
		// TODO Auto-generated constructor stub
	}

	public void parse(XmlPullParser xpp) throws XmlPullParserException, IOException{
		Map<String,String> attributes;
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tagname = xpp.getName();
			switch(eventType){
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.END_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if(tagname.equalsIgnoreCase("route")){
						try {
							attributes = getAttributes(xpp);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			case XmlPullParser.TEXT:
				break;
			}
			eventType = xpp.next();
		}
	}
	
	private Map<String,String>  getAttributes(XmlPullParser parser) throws Exception {
	    Map<String,String> attrs=null;
	    int acount=parser.getAttributeCount();
	    if(acount != -1) {
	        attrs = new HashMap<String,String>(acount);
	        for(int x = 0;x < acount;x++) {
	            attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
	        }
	    }
	    else {
	        throw new Exception("No attributes");
	    }
	    return attrs;
	}
}
