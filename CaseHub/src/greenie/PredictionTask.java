package greenie;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

//AsyncTask to fetch predictions for a given stop.
//Returns formatted string of "Next bus: Xm, Xm, Xm"
public class PredictionTask extends AsyncTask<String, Void, String> {

	private final String predURL = "http://www.nextbus.com/predictor/fancyNewPredictionLayer.jsp?a=case-western&r=%s&d=%s&s=%s";
	
	private String getPrediction(String route, String direction, String stop){
		Document doc = new Document("");
		StringBuilder prediction = new StringBuilder("Next bus: ");
		String formatURL = String.format(predURL, route, direction, stop);
		try {
			doc = Jsoup.connect(formatURL).get();
			Elements times = doc.getElementsByClass("right");
			switch(times.size()){
			case 0:
				//No current prediction
				prediction.append("No current prediction");
				break;
			case 2:
				//First prediction is "Arriving"
				prediction.append("Arriving,");
				prediction.append(times.get(0).text() + "m,");
				prediction.append(times.get(1).text() + "m");
				break;
			case 3:
				prediction.append(times.get(0).text() + "m,");
				prediction.append(times.get(1).text() + "m,");
				prediction.append(times.get(2).text() + "m");
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prediction.toString();
	}

	@Override
	protected String doInBackground(String... params) {
		return getPrediction(params[0], params[1], params[2]);
	}

}
