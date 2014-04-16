package schedule.login;

import java.util.ArrayList;

import schedule.ScheduleEvent;

import casehub.MainActivity;

public class LoginHelper {
	
	MainActivity activity;
	
	public LoginHelper(MainActivity activity) {
		this.activity = activity;
	}
	
	public void createLoginTask(String user, String pass) {
	
		new LoginTask().execute(user, pass);
		
	}
	
	private void onLoginComplete(ArrayList<ScheduleEvent> events) {
		//activity.onScheduleLogin(events);
	}

}
