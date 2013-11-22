package person.interfaces;

import person.Location;
import person.Position;
import agent.Role;

public interface Person {
	
	public abstract  void msgNewHour(int hour);//from the world timer

	public abstract void msgAtDest(Position destination); // From the gui. now we can send the correct entrance message to the location manager

	public abstract void msgFinishedEvent(Role r); //The location manager will send this message as the persons role leaves the building
	
}
