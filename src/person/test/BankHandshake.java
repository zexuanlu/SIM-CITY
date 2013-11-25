package person.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import person.Bank;
import person.SimEvent;
import person.PersonAgent;
import person.Position;
import person.SimEvent.EventType;
import person.Location.LocationType;
import bank.*;
import bank.test.mock.*;

public class BankHandshake extends TestCase{
	
	PersonAgent person;
	MockBankHost host;
	SimEvent goToBank;
	Bank bank;
	Position p = new Position(10, 10);
	
	public void setUp() throws Exception{
		
		super.setUp();	
		person = new PersonAgent();
		person.setName("Grant");
		host = new MockBankHost("Gil");
		bank = new Bank("Bank", host, p, LocationType.Bank);
		goToBank = new SimEvent(bank, 1, 9, EventType.CustomerEvent);
	}
	
	@Test
	public void testEntrance() {
		
		//Pre : Check event queue and activeRole
		
		assertTrue("The person we are testing (person) should have no events at creation, it does", person.toDo.peek() == null);
		assertTrue("person should have no active roles at creation, activeRole is true", !person.active());
		
		person.msgNewHour(9); 
		assertTrue("person's time should be 9, it is not", person.getTime() == 9);
		
		//Add the goToRestaurant event
		person.toDo.offer(goToBank);
		assertTrue("person's toDo should now contain goToRestaurant, it does not", person.toDo.peek() == goToBank);
		assertTrue("person's scheduler should return true because we have added one event to his queue", person.pickAndExecuteAnAction());
		
		//Check customer role creation is correct
		assertTrue("person should now have a customer role in his roles list, he does not", person.roles.get(0).role instanceof BankCustomerRole);
		assertTrue("the customer's person pointer should be equivalent to person it is not", person.roles.get(0).role.getPerson() == person);
		
		//the activity beyond the entrance up until exit is up to the person in charge of said role so we needn't test that
		
		//Now test whether the person scheduler runs or blocks
		assertTrue("person's activeRole should be true, it is not", person.active());
		//assertFalse("person's scheduler should block if we run it because role's scheduler should return false", person.pickAndExecuteAnAction());
		person.msgFinishedEvent(person.roles.get(0).role);
		assertTrue("The person's activeRole should return false, it is still true", person.activeRole == false);
		assertTrue("The person's customer role should be false, it is not it is active still", person.roles.get(0).isActive == false);
	}

}
