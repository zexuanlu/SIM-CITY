package person.interfaces;

import person.SimEvent;
import person.Location;
import person.Position;
import agent.Role;

public interface Person {
	
	public abstract  void msgNewHour(int hour);//from the world timer

	public abstract void msgAtDest(Position destination); // From the gui. now we can send the correct entrance message to the location manager

	public abstract void msgFinishedEvent(Role r); //The location manager will send this message as the persons role leaves the building
	
	public abstract void msgAddMoney(int money); //give money back to the person's wallet 

	public abstract void msgAddEvent(SimEvent e); //add an event to the person's scheduler
	
	public abstract void msgReadyToWork(Role r); //notify a waiting person that they can send their role to work
	
	public abstract void msgGoOffWork(Role r, int pay); //return the person's role and have them continue with their day
	
}
