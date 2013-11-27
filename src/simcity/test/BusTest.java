package simcity.test;
import simcity.BusRole; 
import simcity.CityMap; 
import simcity.interfaces.Passenger; 
import simcity.test.Mock.MockBusStop; 
import simcity.test.Mock.MockPassengerRole; 
import junit.framework.*;
import java.util.*;

//Busses are very simple agents that only interact with one other agent, PassengerAgent 
//the test for this is limited to one real scenario without the accidents which haven't been implemented yet
public class BusTest extends TestCase{
	CityMap citymap = new CityMap(); //TEMPORARY SO SO TEMPORARY 
	BusRole busrole; 
	MockBusStop busstop; 
	MockBusStop busstop2;
	MockPassengerRole passenger1; 
	MockPassengerRole passenger2; 
	List<Passenger> sentpassengers = new ArrayList<Passenger>(); 
	List<String>busroute = new ArrayList<String>();

	
	public void setUp() throws Exception{
		super.setUp();
		busrole = new BusRole("BusRole");
		busstop = new MockBusStop("BusStop");
		busstop2 = new MockBusStop("BusStop2");
		passenger2 = new MockPassengerRole("passenger");
		passenger1 = new MockPassengerRole("PBS");
		busroute.add("Stop1");
		busroute.add("Stop2");
		citymap.addBusStop("Stop1", busstop);
		citymap.addBusStop("Stop2", busstop2);
		sentpassengers.add(passenger1);
		
	}
	
	public void testOneNormalCustomerScenario()
	{
		//this is only the bus/customer interactions without any of the nasty busstop stuff
		//only one customer in busstop list, must check later what happens if multiple customers in busstop list
		//initialize bus at first stop
		
		//test initial conditions 
		assertEquals("Busstop should have nothing in it's log", busstop.log.size(),0);
		assertEquals("Passenger should have nothing in it's log", passenger1.log.size(),0);
		assertEquals("Passenger should have nothing in it's log", passenger2.log.size(),0);
		assertEquals("Bus should have no passengers in his list",busrole.passengers.size(),0);
		assertFalse("Bus should have no events in its scheduler",busrole.pickAndExecuteAnAction());
		
		//initializers for bus
		busrole.setCapacity(5); //can fit 5 people; 
		busrole.setFare(10); //fare is 10
		busrole.currentbusstop = busstop; 
		busrole.RouteA = busroute;
		busrole.setCurrentStop("Stop1");
		busrole.setBusMap(citymap);
		//test involving bus arriving at busstop and receiving one passenger from busstop
		busrole.msgHereisList(sentpassengers); 
		assertEquals("Bus should have 1 passenger in his list", busrole.passengers.size(),1);
		assertTrue("Bus should now react to that 1 passenger",busrole.pickAndExecuteAnAction());
		assertTrue("Bus should send message to passenger1 with fare",passenger1.log.containsString("Bus has sent fare of 10"));
		
		//add another customer, this one not from the busstop
		//but like a normal bus, he should handle first passenger before he goes to the next one
		busrole.msgCanIComeOnBus(passenger2);
		busrole.msgHeresMyFare(passenger1, 10); //have passenger pay, bus should handle payment first before handling second customer
		assertEquals("Bus should have 2 passengers in his list",busrole.passengers.size(),2);	
		assertTrue("Bus should now react to the first passenger",busrole.pickAndExecuteAnAction());
		assertEquals("The second customer's log should be empty",passenger2.log.size(),0);
		assertTrue("Bus should have sent message to first customer welcoming him onboard",passenger1.log.containsString("Welcomed onboard"));
		
		assertTrue("Bus should now react to the second passenger",busrole.pickAndExecuteAnAction());
		assertTrue("Bus should send message to passenger2 with fare",passenger2.log.containsString("Bus has sent fare of 10"));
		assertFalse("Bus should have no events in its scheduler",busrole.pickAndExecuteAnAction());
		
		busrole.msgHeresMyFare(passenger2, 10);
		assertTrue("Bus should now react to the second passenger",busrole.pickAndExecuteAnAction());
		assertTrue("Bus should have sent message to second customer welcoming him onboard",passenger2.log.containsString("Welcomed onboard"));
		assertFalse("Bus should have nothing to do at this point",busrole.pickAndExecuteAnAction());
		
		//generally timer calls this next action, but since there was no initial "arriving at the stop" since no gui and bus was created at stop, have to call it here
		busrole.msgtimetoLeave();
		assertTrue("Bus should now react to message from timer to leave",busrole.pickAndExecuteAnAction());
		assertTrue("Bus should have sent message to first busstop telling him that he's leaving", busstop.log.containsString("Bus Leaving"));
		assertTrue("Bus should now react to message from continued action",busrole.pickAndExecuteAnAction());
		//the next action that bus should do is to send a message to the next bus stop that he is here
		assertEquals("busstop should have next currentbusstop",busrole.currentbusstop,busstop2);
		assertEquals("busstop should also have next currentstop",busrole.currentStop,"Stop2");
		assertTrue("Bus should have sent message to second busstop telling it that he's arrived",busstop2.log.containsString("Bus at Stop"));
		assertTrue("Bus should now continue the action by telling all passengers that he's arrived at the stop",busrole.pickAndExecuteAnAction());
		assertTrue("Bus should send message to passenger1",passenger1.log.containsString("At stop Stop2"));
		assertTrue("Bus should send message to passenger2",passenger2.log.containsString("At stop Stop2"));
		assertFalse("Bus should have nothing to do at this point",busrole.pickAndExecuteAnAction());
		
		//testing if people can leave
		busrole.msgLeaving(passenger1);
		assertTrue("Bus should react to leaving passenger",busrole.pickAndExecuteAnAction());
		assertEquals("Bus should have 1 passenger in his list",busrole.passengers.size(),1);

	}

}
