package person.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import market.interfaces.MarketCustomer;
import market.test.mock.MockCashier;

import org.junit.Test;

import agent.TimeCard;
import bank.BankTellerRole;
import bank.test.mock.MockBankHost;
import person.Bank;
import person.Market;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;

public class MultipleEvents extends TestCase{
	PersonAgent person = new PersonAgent("Grant");
	
	Bank bank = new Bank("Banco Popular", new TimeCard(), new MockBankHost("Gil"), 
			new Position(100, 50), LocationType.Bank);
	Market market = new Market("Pokemart", new MockCashier("Oak"), new TimeCard(), 
			new Position(30, 70), LocationType.Market);
	SimEvent goToBank;
	SimEvent goToMarket;
	
	public void setUp() throws Exception{
		super.setUp();	
		goToBank = new SimEvent(bank, 1, 9, 12, EventType.TellerEvent);
		goToMarket = new SimEvent("Buy some stuff", market, 2, EventType.CustomerEvent);
		person.setTime(7);
		goToMarket.setStart(person.getTime());
	}
	@Test
	public void testMultipleEvents() {
		person.testMode = true;
		assertEquals("The current time should be 7am it is not", person.getTime(), 7);
		assertTrue("The event queue should be empty, it is not", person.toDo.peek() == null);
		assertTrue("The person should have 5000 dollars on hand", person.wallet.getOnHand() == 5000);
		person.toDo.offer(goToBank);
		person.toDo.offer(goToMarket);
		
		assertTrue("The goToMarket even should be at the top of the stack, it is not", person.toDo.peek() == goToMarket);
		assertTrue("The person's scheduler should return true and go into the goToAndDoEvent() action, it doesnt", 
				person.pickAndExecuteAnAction());
		assertTrue("The goToBank even should be at the top of the stack, it is not", person.toDo.peek() == goToBank);
		assertTrue("The person should have the customer role in his roles list, he does not", person.roles.get(0).role instanceof MarketCustomer);
		assertTrue("The person's scheduler should running the role's pickandexecute, it is not, rather it is running the persons", 
				 person.active());
		person.msgFinishedEvent(person.roles.get(0).role);
		person.msgNewHour(9);
		assertTrue("The person should now run his own code", person.pickAndExecuteAnAction());
		person.msgReadyToWork(person.roles.get(1).role);
		assertTrue("The person should now have the teller role active, he does not", person.active() && person.roles.get(1).role instanceof BankTellerRole);
		person.msgGoOffWork(person.roles.get(1).role, 100);
		assertTrue("The person should now have one hundred dollars more in his wallet", person.wallet.getOnHand() == 5100);
		
		//if we add another bank teller event to the person queue it should not make a new one, rather it should use the existing one
		person.toDo.offer(goToBank);
		person.pickAndExecuteAnAction();
		assertTrue("The person should not add another role to the list he should find and use the one he has already, the list size increased", 
				person.roles.size() == 2);
	}

}
