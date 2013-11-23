package person.tests;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import person.Event.EventType;
import person.Location.LocationType;
import person.interfaces.*;

import org.junit.Test;

import person.Bank;
import person.Location;
import person.Event;
import person.Market;
import person.PersonAgent;
import person.Position;
import person.Restaurant;
import person.tests.mock.*;

/* Tests the comparator's compare method and the composite score computation used in ordering the person's 
 * priority queue of things to do 
 * 
 * @author Grant Collins
 */

public class EventComparatorTest extends TestCase{
	
	PersonAgent person = new PersonAgent();
	BankHost h1;
	MockHostRole h2;
	MockHostRole h3;
	
	Position p1 = new Position(10, 10);
	Position p2 = new Position(20, 20);
	Position p3 = new Position(30, 30);
	
	Bank bank = new Bank("B1", h1, p1, LocationType.Bank);
	Restaurant work = new Restaurant("B2", h2, p2, LocationType.Restaurant);
	Market market = new Market("B3", h3, p3, LocationType.Market);
	
	//Events to be enetered into pq
	Event goToBank;
	Event goToWork;
	Event goToMarket;
	Event goHome;
	
	
	public void setUp() throws Exception{
		super.setUp();	
		goToBank = new Event("BANK", bank, 2, EventType.CustomerEvent);
		
		goToWork = new Event(work, 1, 10, 14, EventType.HostEvent);
		goToMarket = new Event("MARKET", market, 3, EventType.CustomerEvent);
		
		goHome = new Event(market, 1, 18, 24, EventType.HomeEvent);
	}	
	@Test
	public void testEventScoring() {
		
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		person.msgNewHour(7); //wakes up at 7 am
		goToBank.setStart(person.getTime());
		goToMarket.setStart(person.getTime());
		/*
		 * Preconditions: 
		 * 1.Check that there are no events in the pq at start up 
		 * 2.Check that the persons time is as expected (7am)
		 * 3.Add events to the queue
		 */
		assertEquals("The current time should be 7am it is not", person.getTime(), 7);
		assertTrue("The event queue should be empty, it is not", person.toDo.peek() == null);
		
		person.toDo.offer(goToWork);
		person.toDo.offer(goHome);
		person.toDo.offer(goToBank);
		person.toDo.offer(goToMarket);

		assertTrue("There should be three times in the queue, there are not", person.toDo.size() == 4);
		/* Now test the order while removing */
		System.out.println(person.toDo.peek().type);
		assertTrue("goToBank should be on the top of the list, it is not", person.toDo.poll() == goToBank);
		System.out.println(person.toDo.peek().type);
		assertTrue("goToMarket should be on the top of the list, it is not", person.toDo.poll() == goToMarket);
		System.out.println(person.toDo.peek().type);
		assertTrue("goToWork should be on the top of the list, it is not", person.toDo.poll() == goToWork);
		
	}

}
