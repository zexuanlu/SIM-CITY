package person;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import person.interfaces.Person;
 //msgEndOfDay()
public class SimWorldClock {
	private Timer clock = new Timer();
	public List<PersonAgent> people;
	private int currentHour;
	public int endOfDay;

	public SimWorldClock(int currentHour, List<PersonAgent> people){
		this.currentHour = currentHour;
		this.people = people;
		for(PersonAgent person : people){
			person.msgNewHour(currentHour);
		}
		clock.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateWorldClock();
			}
		}, 0, 60000); // one minute per hour 
	}
	private void updateWorldClock(){
		System.out.println("Time is now " + currentHour );
		for(PersonAgent person : people){
			person.msgNewHour(currentHour);
		}
		if(currentHour != 24){
			currentHour++;
		}
		else{ currentHour = 1; }
	}
	public void addPerson(PersonAgent p){
		people.add(p);
	}
	public int getCurrentTime() {
		return currentHour;
	}
}
