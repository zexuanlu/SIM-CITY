package person.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import person.SimEvent;
import person.Location;
import person.Position;
import person.PersonAgent;
import person.SimEvent.EventType;
import person.Location.LocationType;
import person.Restaurant;
import restaurant1.Restaurant1CustomerRole;
import restaurant1.Restaurant1HostRole;
import person.interfaces.*;
import person.test.mock.MockHostRole;

/*
 * Tests the PersonAgent's ability to switch to a certain role and the entrance handshake between 
 * the person and the host of the particular location
 * 
 * @author Grant Collins
 */
public class PersonRestaurantEntrance extends TestCase{

	PersonAgent person;
	MockHostRole host;
	SimEvent goToRestaurant;
	Location rest;
	Position p = new Position(10, 10);
	
	public void setUp() throws Exception{
		
		super.setUp();	
		person = new PersonAgent();
		PersonAgent dummyPerson = new PersonAgent();
		person.setName("Grant");
		host = new MockHostRole("Grant");
		rest = new Restaurant("Restaurant", new Restaurant1HostRole("DUMMY", dummyPerson), p, LocationType.Restaurant1);
		goToRestaurant = new SimEvent(rest, 9, EventType.CustomerEvent);
		person.testMode = true;
	}	
	@Test
	public void testRestaurantCustomerEntrance() {
		//Pre : Check event queue and activeRole
		
		assertTrue("The person we are testing (person) should have no events at creation, it does", person.toDo.isEmpty());
		assertTrue("person should have no active roles at creation, activeRole is true", !person.active());
		
		person.msgNewHour(9); 
		assertTrue("person's time should be 9, it is not", person.getTime() == 9);
		
		//Add the goToRestaurant event
		person.toDo.add(goToRestaurant);
		assertTrue("person's toDo should now contain goToRestaurant, it does not", person.toDo.get(0) == goToRestaurant);
		assertTrue("person's scheduler should return true because we have added one event to his queue", person.pickAndExecuteAnAction());
		
		//Check customer role creation is correct
		assertTrue("person should now have a customer role in his roles list, he does not", person.roles.get(0).role instanceof Restaurant1CustomerRole);
		assertTrue("the customer's person pointer should be equivalent to person it is not", person.roles.get(0).role.person == person);
		
		//Check that host for the restaurant received our message and both the person and the customer role
		assertTrue("person's scheduler should return true because we have added one role to his queue", person.pickAndExecuteAnAction());
		//the activity beyond the entrance up until exit is up to the person in charge of said role so we needn't test that
		
		//Now test whether the person scheduler runs or blocks
		assertTrue("person's activeRole should be true, it is not", person.active());
		assertFalse("person's scheduler should block if we run it because role's scheduler should return false", person.pickAndExecuteAnAction());
		//the above test is vague atm but check the console and you should see "Killer, im running as a role" for a little extra verification 
	}

}
