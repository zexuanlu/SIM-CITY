package person;

import gui.subpanels.TracePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import person.Location.LocationType;
import simcity.CityMap;
import utilities.TimeCard;
 //msgEndOfDay()
public class SimWorldClock {
	private Timer clock = new Timer();
	public List<PersonAgent> people;
	public CityMap cityMap;
	public List<TimeCard> timeCards;
	private int hourLength;
	private int currentHour;
	public int endOfDay;
	public int dayOfTheWeek;
	public TracePanel tracePanel = null;

	public SimWorldClock(int currentHour, List<PersonAgent> people, CityMap cm, int time){
		this.currentHour = currentHour;
		timeCards = new ArrayList<TimeCard>();
		//hourLength = time;
		hourLength = 6000; 
		cityMap = cm;
		this.people = people;
		dayOfTheWeek = 1;
		for(PersonAgent person : people){
			person.msgNewHour(currentHour);
		}
		clock.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateWorldClock();
			}

		}, 0, hourLength); // one minute per hour 
	}
	private void updateWorldClock(){
		if(tracePanel != null)
			tracePanel.print("Time is now " + currentHour, null);
		for(PersonAgent person : people){
			person.msgNewHour(currentHour);
		}
		if(currentHour == 22){
			closeUp();
			for(TimeCard timeCard : timeCards){
				timeCard.msgEndOfDay();
			}
		}
		if(currentHour != 24){
			currentHour++;
		}
		else{ 
			currentHour = 1;
			dayOfTheWeek++;
		}
	}
	public void closeUp(){
		for(Location l : cityMap.map){
			if( l.type != LocationType.Apartment && l.type != LocationType.Home){
				l.isClosed = true;
			}
		}
	}
	public void addPerson(PersonAgent p){
		people.add(p);
	}
	public int getCurrentTime(){
		return currentHour;
	}
	public int getCurrentDay(){
		return dayOfTheWeek;
	}
}
