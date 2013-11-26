package simcity.test;
import simcity.test.Mock.MockBusRole; 
import simcity.test.Mock.MockPassengerRole; 
import simcity.BusStopAgent; 
import junit.framework.*;

public class BusStopTest extends TestCase{
	BusStopAgent busstop; 
	MockBusRole bus; 
	MockPassengerRole passenger; 
	
	public void setUp() throws Exception{
		super.setUp();
		busstop = new BusStopAgent("busstop");
		bus = new MockBusRole("bus");
		passenger = new MockPassengerRole("passenger");
	}
	
	public void testOneNormalCustomerScenario()
	{
		//Normal interaction
		//check initial conditions of the mocks & busStop
		assertEquals("mockBus log should be empty",0,bus.log.size());
		assertEquals("mockPassenger log should be empty",0,passenger.log.size()); 
		assertEquals("busStop should have no passengers",0,busstop.passengers.size());
		assertEquals("busStop should have no busses",0,busstop.busses.size());
		assertFalse("busStop should recognize that no bus has arrived yet",busstop.isBusAtStop(bus));
		assertFalse("busStop scheduler has nothing to do",busstop.pickAndExecuteAnAction());
		//now add a bus to the busStop (hasn't arrived yet.) 
		busstop.addBus(bus);
		assertFalse("busStop scheduler has nothing to do",busstop.pickAndExecuteAnAction());
		assertEquals("busStop should have one bus in list",1,busstop.busses.size());
		assertFalse("busStop should recognize that no bus has arrived yet",busstop.isBusAtStop(bus));
		//now add a passenger and see if busStop adds it to the list
		busstop.msgatBusStop(passenger);
		assertFalse("busStop scheduler has nothing to do",busstop.pickAndExecuteAnAction());
		assertEquals("busStop should have one passenger in list",1,busstop.passengers.size());
		//now have the bus arrive
		busstop.msgatBusStop(bus);
		assertTrue("busStop should recognize that bus has arrived",busstop.isBusAtStop(bus));	
		assertTrue("busStop scheduler has to send message back to bus with list of customers",busstop.pickAndExecuteAnAction());
		assertFalse("busStop scheduler has nothing to do",busstop.pickAndExecuteAnAction());
		assertEquals("bus should have received a list of passengers waiting at busstop", bus.passengerlist.size(),1);
		assertTrue("in bus's received list, should have initialized passenger", bus.passengerinList(passenger));
		//now have bus leave
		busstop.msgBusLeaving(bus);
		assertFalse("busStop should recognize that bus is leaving",busstop.isBusAtStop(bus));
		assertFalse("busStop scheduler has nothing to do",busstop.pickAndExecuteAnAction());
		assertEquals("busStop should have cleared its passengerslist",0,busstop.passengers.size());
		assertEquals("busStop should still have one bus in list",1,busstop.busses.size());

	}
}
