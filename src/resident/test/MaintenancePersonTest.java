package resident.test;

import resident.MaintenancePersonAgent;
import resident.test.mock.MockHomeOwner;
import junit.framework.TestCase;

public class MaintenancePersonTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	MaintenancePersonAgent housekeeper;
	MockHomeOwner homeOwner;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		housekeeper = new MaintenancePersonAgent("Mock Maintenance");	
		homeOwner = new MockHomeOwner("HomeOwner", 1);		
	} 
}
