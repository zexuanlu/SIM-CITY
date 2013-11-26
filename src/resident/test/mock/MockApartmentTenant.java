package resident.test.mock;

import resident.interfaces.ApartmentTenant;

public class MockApartmentTenant extends Mock implements ApartmentTenant {
	public EventLog log = new EventLog();
	public int aptNum;
	
	public MockApartmentTenant(String name, int n) {
		super(name);
		aptNum = n;
	}

	public void msgReceivedRent(double amt) {
		log.add(new LoggedEvent("The landlord received my rent. I owe $" + amt + "."));
	}
}
