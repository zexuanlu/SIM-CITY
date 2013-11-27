package person.test.mock;

import person.interfaces.Customer;
import person.test.mock.EventLog;
import person.test.mock.LoggedEvent;

public class MockCustomerRole extends Mock implements Customer{

	public EventLog log = new EventLog();
	
	public MockCustomerRole(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		log.add(new LoggedEvent("I am now a customer role!"));
	}

	@Override
	public void gotHungry() {
		
		log.add(new LoggedEvent("Recieved hungry message"));
		
	}

}
