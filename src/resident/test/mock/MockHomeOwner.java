package resident.test.mock;

import resident.interfaces.HomeOwner; 
import resident.test.mock.EventLog;

public class MockHomeOwner extends Mock implements HomeOwner{

	/**
	 * Reference to the Maintenance Person under test that can be set by the unit test.
	 */
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
	
	public void msgMaintainHome() {		
	}

	public void updateVitals(int i, int j) {
	
	}

}
