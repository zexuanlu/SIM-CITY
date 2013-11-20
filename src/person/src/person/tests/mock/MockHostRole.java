package person.tests.mock;

import java.util.HashMap;
import java.util.Map;

import person.CustomerRole;
import person.HostRole;
import person.PersonAgent;
import person.interfaces.Host;

public class MockHostRole extends Mock implements Host{

	public EventLog log = new EventLog();
	public Map<PersonAgent, CustomerRole> people = new HashMap<PersonAgent, CustomerRole>();
	public MockHostRole(String name) {
		super(name);
		log.add(new LoggedEvent("I am now a Host!"));
	}

	@Override
	public void msgIWantFood(PersonAgent p, CustomerRole cRole) {
		
		people.put(p, cRole);
		log.add(new LoggedEvent("The customer role "+cRole.getName()+" has entered via the person "+p.getName()+" and is hungry"));
		
	}

	@Override
	public void msgClockIn(PersonAgent p, HostRole hRole) {
		
		log.add(new LoggedEvent("The host role "+hRole.getName()+" has entered via the person "+p.getName()+" and is eager to begin work"));
		
	}
	
	//purely a test method for automating the msg send to the personagent waiting on the role
	public void sendFinishedCustomer(PersonAgent p){
		
		p.msgFinishedEvent(people.get(p));
		log.add(new LoggedEvent("I just sent "+p.getName()+" his customer role back"));
		
	}

}
