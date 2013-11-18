package resident.test.mock;

import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;

public class MockMaintenancePerson extends Mock implements MaintenancePerson{

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public HomeOwner homeOwner;
	private double maintenanceCost;
	
	public EventLog log = new EventLog();
	
	// Constructor for the mock maintenance person
	public MockMaintenancePerson(String name) {
		super(name);
	}
	
	// Sets maintenance cost
	public void setMaintenanceCost(double amt) {
		maintenanceCost = amt;
	}
	
	// Gets the maintenance cost
	public double getMaintenanceCost() {
		return maintenanceCost;
	}
	
	// When the home owner tells the maintenance person to go maintain the house
	public void msgPleaseComeMaintain(HomeOwner homeOwnerAgent, int houseNumber) {
		log.add(new LoggedEvent("Received message from home owner " + homeOwnerAgent.getName() + " to maintain house " + houseNumber + "."));
	}
	
	// When home owner tells the maintenance person to come in and maintain the house
	public void msgPleaseComeIn(HomeOwner homeOwnerAgent, int houseNumber) {
		log.add(new LoggedEvent("Received message to go in home " + houseNumber + "."));
	}

	// When the maintenance person has finished maintaining, and is receiving payment from home owner
	public void msgHereIsThePayment(HomeOwner homeOwnerAgent, double maintenanceCost) {
		log.add(new LoggedEvent("Received payment from home owner of " + maintenanceCost + "."));
	}
	
	
}
