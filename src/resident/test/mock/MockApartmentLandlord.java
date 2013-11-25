package resident.test.mock;

import resident.interfaces.ApartmentLandlord; 
import resident.interfaces.ApartmentTenant;

public class MockApartmentLandlord extends Mock implements ApartmentLandlord {
	public EventLog log = new EventLog();

	public MockApartmentLandlord(String name) {
		super(name);
	}
	
	public void msgHereIsTheRent(ApartmentTenant apartmentTenantAgent, double money) {
		log.add(new LoggedEvent("Received rent of $" + money +" from " + apartmentTenantAgent.getName()));
	}

}
