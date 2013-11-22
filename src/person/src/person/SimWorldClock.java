package person;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SimWorldClock {
	Timer clock = new Timer();
	List<PersonAgent> people;
	int currentHour;

	SimWorldClock(int currentHour, List<PersonAgent> people){
		this.currentHour = currentHour;
		this.people = people;
		clock.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateWorldClock();
			}
		}, 0, 60000); // one minute per hour 
	}
	private void updateWorldClock(){
		if(currentHour != 24){
			currentHour++;
		}
		else{ currentHour = 1; }
		for(PersonAgent person : people){
			person.msgNewHour(currentHour);
		}
	}
}
