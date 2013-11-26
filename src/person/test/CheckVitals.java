package person.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import market.test.mock.MockCashier;

import org.junit.Test;

import agent.TimeCard;
import bank.test.mock.MockBankHost;
import person.Bank;
import person.Market;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;
/*
 * Tests the checkVitals() method which picks things to fill a persons free time based on 
 * their hunger and financial standing. 
 * @author Grant Collins
 * */
public class CheckVitals extends TestCase{
	PersonAgent person = new PersonAgent("Grant");
	
	Bank bank = new Bank("Banco Popular", new TimeCard(), new MockBankHost("Gil"), 
			new Position(100, 50), LocationType.Bank);
	Market market = new Market("Pokemart", new MockCashier("Oak"), new TimeCard(), 
			new Position(30, 70), LocationType.Market);
	SimEvent goToBank;
	SimEvent goToMarket;
	
	public void setUp() throws Exception{
		super.setUp();	
		//goToBank = new SimEvent("Get some money", bank, 1, EventType.CustomerEvent);
		//goToMarket = new SimEvent("Buy some stuff", market, 2, EventType.CustomerEvent);
		person.setTime(7);
		//goToMarket.setStart(person.getTime());
		//goToBank.setStart(person.getTime());
	}
	@Test
	public void test() {
		assertEquals("The current time should be 7am it is not", person.getTime(), 7);
		assertTrue("The event queue should be empty, it is not", person.toDo.peek() == null);
		assertTrue("The person should have 5000 dollars on hand", person.wallet.getOnHand() == 5000);
		assertTrue("if we run the persons scheduler we should check our vitals and determine we need to deposit at the bank", 
			person.pickAndExecuteAnAction());
		//no check if checkVitals put an event on our list 
		assertTrue("We should now have one event in our toDo list", person.toDo.peek() != null);
		//is it the bank event to deposit?
		assertTrue("This event should be on the type customerevent, at the location bank and with the directive need deposit", 
				person.toDo.peek().getDirective() == "Need Deposit"
				&& person.toDo.peek().type == EventType.CustomerEvent
				&& person.toDo.peek().location.type == LocationType.Bank);
		
		//From here the person goes through the role switch dance tested in three other tests
		
		
		
	}

}
