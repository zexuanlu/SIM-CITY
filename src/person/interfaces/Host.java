package person.interfaces;

import person.interfaces.CustomerRole;
import person.interfaces.HostRole;
import person.PersonAgent;
import person.test.mock.EventLog;

/* 
 * This is merely a stub host interface for testing and writing the person code
 * It will be replaced with the real, final host code which will come from group members
 * as will the rest of the role interfaces
 */
public interface Host {
	
	EventLog log = new EventLog();
	
	public abstract void msgIWantFood(PersonAgent p, CustomerRole cRole); // for customer interactions 
	
	public abstract void msgClockIn(PersonAgent p, HostRole hRole); // for job interactions 
}
