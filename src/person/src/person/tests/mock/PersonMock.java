package person.tests.mock;

import agent.Role;
import person.Location;
import person.Position;
import person.interfaces.Person;

public class PersonMock extends Mock implements Person {
	
	public EventLog log = new EventLog();
	
	public PersonMock(String name) {
		super(name);
		
	}

	@Override
	public void msgNewHour(int hour) {
		
		log.add(new LoggedEvent("the hour has updated"));
	}

	@Override
	public void msgAtDest(Position destination) {
		
		log.add(new LoggedEvent("you have arrived at your gui destination"));
	}

	@Override
	public void msgFinishedEvent(Role r) {
		
		log.add(new LoggedEvent("Youve finished your role and have returned to your PersonAgent"));
		
	}

}
