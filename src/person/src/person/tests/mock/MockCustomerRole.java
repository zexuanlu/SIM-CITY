package person.tests.mock;

import person.HostRole;
import person.interfaces.Customer;
import person.tests.mock.EventLog;
import person.tests.mock.LoggedEvent;

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

	@Override
	public void msgConfirmed(HostRole hr) {
		
		log.add(new LoggedEvent("The host "+hr.getName()+" of this restaurant has confirmed me as a customer in the building"));
		
	}

}
