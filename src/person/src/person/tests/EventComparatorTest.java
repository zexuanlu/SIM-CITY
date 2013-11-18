package person.tests;

import static org.junit.Assert.*;
import junit.framework.TestCase;
import person.Event.EventType;
import person.Location.LocationType;
import person.interfaces.*;

import org.junit.Test;

import person.Location;
import person.Event;
import person.PersonAgent;
import person.tests.mock.*;

/* Tests the comparator's compare method and the composite score computation used in ordering the person's 
 * priority queue of things to do 
 */

public class EventComparatorTest extends TestCase{
	
	PersonAgent person = new PersonAgent();
	EventLog log;
	
	Location bank = new Location(10, 10, "B1");
	Location work = new Location(20, 20, "B2");
	Location market = new Location(30, 20, "B3");
	
	//Events to be enetered into pq
	Event goToBank;
	Event goToWork;
	Event goToMarket;
	
	
	public void setUp() throws Exception{
		super.setUp();	
		goToBank = new Event(bank, 2, 12, 13, EventType.CustomerEvent); //times in millitary
		goToWork = new Event(work, 1, 9, 11, EventType.HostEvent);
		goToMarket = new Event(market, 3, 15, 16, EventType.CustomerEvent);
		/*
		 * Scores should be 
		 * 
		 * goToBank: 2 + 5 = 7
		 * goToWork: 1 + 2 = 3
		 * goToMarket: 3 + 8 = 11
		 */
		
	}	
	@Test
	public void test() {
		
		try {
			setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		person.msgNewHour(7); //wakes up at 7 am
		/*
		 * Preconditions: 
		 * 1.Check that there are no events in the pq at start up 
		 * 2.Check that the persons time is as expected (7am)
		 * 3.Add events to the queue
		 */
		assertEquals("The current time should be 7am it is not", person.getTime(), 7);
		assertTrue("The event queue should be empty, it is not", person.toDo.peek() == null);
		
		person.toDo.offer(goToWork);
		person.toDo.offer(goToBank);
		person.toDo.offer(goToMarket);
		
		assertTrue("There should be three times in the queue, there are not", person.toDo.size() == 3);
		/* Now test the order while removing */
		System.out.println(person.toDo.peek().type);
		assertTrue("goToWork should be on the top of the list, it is not", person.toDo.poll() == goToWork);
		System.out.println(person.toDo.peek().type);
		assertTrue("goToBank should be on the top of the list, it is not", person.toDo.poll() == goToBank);
		System.out.println(person.toDo.peek().type);
		assertTrue("goToMarket should be on the top of the list, it is not", person.toDo.poll() == goToMarket);
		
	}

}
