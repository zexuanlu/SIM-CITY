package simcity.test;
import simcity.BusRole; 
import simcity.test.Mock.MockBusStop; 
import simcity.test.Mock.MockPassengerRole; 
import junit.framework.*;

public class BusTest extends TestCase{
	BusRole busrole; 
	MockBusStop busstop; 
	MockPassengerRole passenger; 
	
	public void setUp() throws Exception{
		super.setUp();
		busrole = new BusRole("BusRole");
		busstop = new MockBusStop("BusStop");
		passenger = new MockPassengerRole("Passenger");
	}
	
	public void testOneNormalCustomerScenario()
	{
		//check initial conditions of the mocks
		
		
	}
}
