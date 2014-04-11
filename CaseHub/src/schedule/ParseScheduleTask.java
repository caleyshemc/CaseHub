package schedule;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

public class ParseScheduleTask extends AsyncTask<String, Void, ArrayList<ScheduleEvent>> {
	
	@Override
    protected ArrayList<ScheduleEvent> doInBackground(String... args) {
        return parseSchedule(args[0]);
    }
	
	private ArrayList<ScheduleEvent> parseSchedule(String html) {
		ArrayList<ScheduleEvent> scheduleEvents = new ArrayList<ScheduleEvent>();
		Document doc = Jsoup.parse(html);

		// For each day of the week
		for (Day day : Day.values()) {
			
			// Select each event in this day
			Element div = doc.getElementById(day.toString());
			Elements events = div.select(".event");
			
			// Create ScheduleEvents
			for (Element event : events) {
				
				// Get raw event info
				String name = event.select(".eventname").first().text();
				String times = event.select(".timespan").first().text();
				String location = event.select(".location").first().text();
				
				// Get event ID by extracting digits from 'onclick' attribute
				String idString = event.attr("onclick");
				idString = idString.replaceAll("\\D+","");
				int id = Integer.parseInt(idString);
				
				// Extract start/end times
				String[] split = times.split("-");
				String start = split[0] + "m";
				String end = split[1] + "m";
								
				ScheduleEvent newEvent =  new ScheduleEvent(id, name, location, start, end, day);
				
				scheduleEvents.add(newEvent);
				
			}
			
		}
		
		return scheduleEvents;
	}

}