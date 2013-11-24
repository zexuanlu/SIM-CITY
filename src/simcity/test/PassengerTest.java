package simcity.test;
import simcity.test.Mock.MockBusRole;
import simcity.test.Mock.MockBusStop; 
import simcity.test.Mock.PersonMock; 
import simcity.PassengerRole;
import junit.framework.*;

public class PassengerTest extends TestCase{
	MockBusStop busstop; 
	PersonMock person; 
	MockBusRole bus; 
	PassengerRole passenger; 
	String Destination;
	double busfare; 
	
	public void setUp() throws Exception{
		super.setUp();
		busfare = 10; 
		Destination = "Palo Alto";
		passenger = new PassengerRole("passenger",person);
		bus = new MockBusRole("bus");
		busstop = new MockBusStop("busstop");
	}
	
	public void testOneNormalCustomerScenario()
	{
		//normative scenario, bus at bus stop, waits one stop then gets off 
		//has enough cash, bus is not at maximum capacity
		
		//check initial conditions
		assertEquals("bus' log is initially empty",bus.log.size(),0);
		assertEquals("busstop's log is initially empty",0,busstop.log.size());
		assertFalse("passenger initially has no actions",passenger.pickAndExecuteAnAction());
		
		//setting the passenger's busroute so he knows where to go etc.
		//usually this part would have been done when passenger is created
		passenger.setDestination(Destination);
		passenger.setBus(bus);
		passenger.setBusStop(busstop);
		
		//sending first message to passenger so that he goes to the bus
		passenger.gotoBus();
		assertTrue("passenger needs to react to the new Event",passenger.pickAndExecuteAnAction());
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		//for the purpose of this normative test of passenger, assume that bus is already at stop. 
		assertTrue("passenger should have sent message to bus asking to come aboard",bus.log.containsString("Can I come on Bus"));
		
		//now passenger receives message from bus with the fare
		passenger.msgHereIsPrice(bus, busfare);
		assertTrue("passenger needs to react to the new Event",passenger.pickAndExecuteAnAction());
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		assertTrue("passenger should have sent correct amount back to bus", bus.log.containsString("Passenger paid amount of 10"));
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());

		//receives message confirming that his fare was handled and that he is now allowed on the bus
		passenger.msgComeOnBus(bus);
		assertTrue("passenger needs to react to the new Event",passenger.pickAndExecuteAnAction());
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		//purely gui actions for passenger boarding bus, no response message to bus
		
		//now message sent to passenger that he is at a stop, not the destination stop
		//customer should not react
		passenger.msgNowAtStop("Cupertino");
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		
		//message that customer is at the correct stop
		passenger.msgNowAtStop("Palo Alto");
		assertTrue("passenger needs to react to the new Event",passenger.pickAndExecuteAnAction());
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		assertTrue("passenger sent message to bus saying that he's leaving",bus.log.containsString("Passenger leaving bus"));
		
		assertFalse("passenger has no actions",passenger.pickAndExecuteAnAction());
		
	}
}