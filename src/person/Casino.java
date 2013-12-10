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
	int lengthOfStay = 2880000;
	
	public Casino(List<PersonAgent> gamblers, String name, Position p, LocationType lt){
		super(name, lt, p);
		duration = new Timer();
		luckyWinner = new Random();
		this.gamblers = gamblers;
	}
	public void startTimer(){
		duration.schedule(new TimerTask() {
			public void run() {
				sendGamblersHome();
			}
		}, lengthOfStay);
	}
	public void sendGamblersHome(){
		for(PersonAgent p : gamblers){
			p.msgGoHome();
		}
		pickTheWinner().msgAddMoney(2500.00);
	}
	public PersonAgent pickTheWinner(){
		int winner = luckyWinner.nextInt(gamblers.size());
		return gamblers.get(winner);
	}
}
