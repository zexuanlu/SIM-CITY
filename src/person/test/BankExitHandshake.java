package person.test;

import static org.junit.Assert.*;
import agent.*;
import junit.framework.TestCase;

import org.junit.Test;

import bank.*;
import person.Bank;
import person.SimEvent;
import person.PersonAgent;
import person.Position;
import person.SimEvent.EventType;
import person.Location.LocationType;
import bank.test.mock.*;

public class BankExitHandshake extends TestCase{
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
		TimeCard tc = new TimeCard();
		tc.startThread();
		bank = new Bank("Bank", tc, host, p, LocationType.Bank);
		goToBank = new SimEvent(bank, 1, 9, EventType.TellerEvent);
		
	}
	
	@Test
	public void testEntranceWorkHandshake() {
		assertTrue("The person we are testing (person) should have no events at creation, it does", person.toDo.peek() == null);
		assertTrue("person should have no active roles at creation, activeRole is true", !person.active());
		
		person.msgNewHour(9); 
		assertTrue("person's time should be 9, it is not", person.getTime() == 9);
		
		//Add the goToRestaurant event
		person.toDo.offer(goToBank);
		assertTrue("person's toDo should now contain goToBank, it does not", person.toDo.peek() == goToBank);
		assertTrue("person's scheduler should return true because we have added one event to his queue", person.pickAndExecuteAnAction());
		
		//Check customer role creation is correct
		assertTrue("person should now have a TellerRole role in his roles list, he does not", person.roles.get(0).role instanceof BankTellerRole);
		assertTrue("the customer's person pointer should be equivalent to person it is not", person.roles.get(0).role.getPerson() == person);
		
		//Check that host for the restaurant received our message and both the person and the customer role
		/*assertTrue("host's log should read: The customer role Grant has entered via the person Grant and is hungry, instead it reads: "+host.log.getLastLoggedEvent().getMessage(), 
					host.log.containsString("The customer role Grant has entered via the person Grant and is hungry"));*/
		//the activity beyond the entrance up until exit is up to the person in charge of said role so we needn't test that
		//assertTrue("");
		//Now test whether the person scheduler runs or blocks
		assertTrue("person's activeRole should be true, it is not", person.active());
		assertFalse("person's scheduler should block if we run it because role's scheduler should return false", person.pickAndExecuteAnAction());
		//the above test is vague atm but check the console and you should see "Killer, im running as a role" for a little extra verification
	}

}
