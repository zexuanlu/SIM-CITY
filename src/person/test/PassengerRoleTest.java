package person.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import bank.test.mock.MockBankHost;
import person.Bank;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;
import simcity.PassengerRole;
/* 
 * Test requires a cityMap object for success (or just if true in place of !inWalkingDistance)
 * Tests the ability of the person to switch back and forth between a person and a passenger successfully
 * 
 * @author Grant Collins
 */
public class PassengerRoleTest extends TestCase{
	PersonAgent person;
	MockBankHost host;
	SimEvent goToBank;
	Bank bank;
	Position p = new Position(100, 100);
	
	
	public void setUp() throws Exception{
		super.setUp();
		person = new PersonAgent();
		person.setName("Grant");
		host = new MockBankHost("Gil");
		bank = new Bank("Bank", host, p, LocationType.Bank);
		goToBank = new SimEvent(bank, 1, 9, 10, EventType.CustomerEvent);
	}
	@Test
	public void testPassengerRole() {
		assertTrue("The person we are testing (person) should have no events at creation, it does", person.toDo.peek() == null);
		assertTrue("person should have no active roles at creation, activeRole is true", !person.active());
		
		person.msgNewHour(9); 
		assertTrue("person's time should be 9, it is not", person.getTime() == 9);
		
		//Add the goToRestaurant event
		person.toDo.offer(goToBank);
		assertTrue("person's toDo should now contain goToBank, it does not", person.toDo.peek() == goToBank);
		assertTrue("person's scheduler should return true because we have added one event to his queue", person.pickAndExecuteAnAction());
		
		assertTrue("person's role list should have a passenger role in it, it does not", person.roles.get(0).role instanceof PassengerRole);
		assertTrue("person shoul dhave activeRole == true, it is false", person.activeRole);
		person.msgAtDest(100, 100);
		assertTrue("person's current location should be 100 100 it is not", 
				person.currentLocation.getX() == 100 && person.currentLocation.getY() == 100);
		assertTrue("person's should be executing as a passenger role, it is not", person.pickAndExecuteAnAction());
		
	}

}
