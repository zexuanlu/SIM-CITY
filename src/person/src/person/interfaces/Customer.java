package person.interfaces;

import person.HostRole;
import person.tests.mock.EventLog;

/**
 * A Customer interface built to unit test a PersonAgent.
 *
 * @author Grant Collins
 *
 */
public interface Customer {
	
	EventLog log = new EventLog();
	
	public abstract void gotHungry();

	public abstract String getName();
	
	public abstract void msgConfirmed(HostRole hr);
	
}