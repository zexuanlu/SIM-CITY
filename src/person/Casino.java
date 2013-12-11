package person;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import person.SimEvent.EventType;
/**
 * The Casino location. 
 * 1.Tracks the time of the people's stay (weekend)
 * 2.Chooses a random lucky winner from the patrons and pays him his winnings
 * (Enough to buy a car)
 * @author Grant Collins
 */
public class Casino extends Location{
	Timer duration;
	Random luckyWinner;
	List<PersonAgent> gamblers;
	int lengthOfStay = 10000;
	public boolean hasStarted; 

	public Casino(List<PersonAgent> gamblers, String name, Position p, LocationType lt){
		super(name, lt, p);
		duration = new Timer();
		luckyWinner = new Random();
		this.gamblers = gamblers;
		hasStarted = false;
	}

	public void startTimer(){
		if(!hasStarted){
			hasStarted = true;
			duration.schedule(new TimerTask() {
				public void run() {
					sendGamblersHome();
				}
			}, lengthOfStay);
		}
	}
	public void sendGamblersHome(){
		for(PersonAgent p : gamblers){
			p.msgGoHome("Casino");
		}
		pickTheWinner().msgAddMoney(2500.00);
	}
	public PersonAgent pickTheWinner(){
		int winner = luckyWinner.nextInt(gamblers.size());
		PersonAgent p = gamblers.get(winner);
		System.out.println("WINNING "+p.getName());
		return p;
	}
}
