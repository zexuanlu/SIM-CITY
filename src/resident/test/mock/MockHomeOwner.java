package resident.test.mock;

import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;

public class MockHomeOwner extends Mock implements HomeOwner{

	/**
	 * Reference to the Maintenance Person under test that can be set by the unit test.
	 */
	public MaintenancePerson housekeeper;
	public String name;
	public int houseNum;
	public double myMoney;
	public double debt;
	
	public EventLog log = new EventLog();
	
	// Constructor for the mock maintenance person
	public MockHomeOwner(String n, int i) {
		super(n);
		name = n;
		houseNum = i;
	}

	public void msgDoneMaintaining(double maintenanceAmount) {
		if (myMoney >= maintenanceAmount) {
			log.add(new LoggedEvent("Received message that housekeeper is done maintaining my home. I now have to pay " + maintenanceAmount + "."));
			myMoney -= maintenanceAmount;
		}
		else {
			log.add(new LoggedEvent("Received message that housekeeper is done maintaining my home, but I don't have enough money. I'll just pay " + myMoney + "."));
			myMoney = 0;
		}
	}

	public void msgReceivedPayment(double amt) {
		debt += amt;
		log.add(new LoggedEvent("My debt is now " + amt + "."));
	}
	
}
