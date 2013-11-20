package person.tests;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import person.CustomerRole;
import person.Event;
import person.Location;
import person.PersonAgent;
import person.Event.EventType;
import person.Location.LocationType;
import person.tests.mock.MockCustomerRole;
import person.tests.mock.MockHostRole;
/*
 * Tests the exit handshake between a host and a PersonAgent as the person's role leaves the location
 *
 * @author Grant Collins
 */
public class PersonRestaurantExit extends TestCase{
	
	PersonAgent person;
	MockHostRole host;
	CustomerRole customer;
	
	public void setUp() throws Exception{
		
		super.setUp();	
		person = new PersonAgent();
		person.setName("Grant");
		customer = new CustomerRole("Grant", person);
		person.addRole(customer);
		host = new MockHostRole("Gil");
		host.people.put(person, customer);
		person.activeRole = true;
	}
	
	@Test
	public void testExit() {
		//Pre: The host has the person's role, the person's activeRole is true
		assertTrue("host should have person -> customer as a kv pair in people, it doesn't", host.people.get(person) == customer);
		assertTrue("person's activeRole should be true, it is false", person.active());
		
		//Message reception
		host.sendFinishedCustomer(person);
		assertTrue("host's latest log should read: I just sent Grant his customer role back, instead it reads: "+host.log.getLastLoggedEvent().getMessage(), 
					host.log.containsString("I just sent Grant his customer role back"));
		assertFalse("person should now have his role back and should have deactivated it for the time being, this is not the case", 
					person.roles.get(0).isActive());
		assertFalse("person's scheduler should run its own events now, it is still blocked by activeRole", person.activeRole);
		assertFalse("person's scheduler should block as we have no events in the queue", person.pickAndExecuteAnAction());
	}

}
