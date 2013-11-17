package resident.test.mock;

import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;

public class MockMaintenancePerson extends Mock implements MaintenancePerson{
	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public HomeOwner homeOwner;
	
	public EventLog log = new EventLog();
	
	// Constructor for the mock maintenance person
	public MockMaintenancePerson(String name) {
		super(name);
	}
	
	// When the home owner tells the maintenance person to go maintain the house
	public void msgPleaseComeMaintain(HomeOwner homeOwnerAgent, int houseNumber) {
		
	}

	// When the maintenance person has finished maintaining, and is receiving payment from home owner
	public void msgHereIsThePayment(HomeOwner homeOwnerAgent, double maintenanceCost) {
		
	}
	
	
	
}
