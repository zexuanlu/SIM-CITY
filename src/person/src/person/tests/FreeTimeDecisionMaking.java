package person.tests;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import person.Event;
import person.PersonAgent;
import person.Position;
import person.Event.EventType;
import person.tests.mock.MockHostRole;
/* Tests the ability of the PersonAgent to make decisions in his free time like: robbing the bank
 * buying a car, going home, going to eat, etc;
 * 
 * @author Grant Collins
 */
public class FreeTimeDecisionMaking extends TestCase{
	PersonAgent person;
	CitySetUpHelper citySetUp = new CitySetUpHelper();

	public void setUp() throws Exception{
		super.setUp();	
		person = new PersonAgent("Grant", citySetUp.getLocationList());
		List<Event> events = citySetUp.getEventList();
		for(int i=0; i < events.size(); i++){
			person.toDo.offer(events.get(i));
		}
	}
	@Test
	public void testFreeTime() {

		/*Pre: 
		 * Check that the person has three events in his queue. 
		 * Check that there are seven locations on the city map
		 * Check that the person has $5,000 cash in his wallet and in bank
		 * Check that the person has no roles active (!activeRole)
		 * Check that the priority queue is in order
		 */
		assertTrue("There should be 3 events in the person's toDo list, there are not", person.toDo.size() == 3);
		assertTrue("There should be 7 locations in the person's cityMap list there are not instead there are: "+person.cityMap.map.size(), 
				person.cityMap.map.size() == 7);
		assertTrue("There should be $5,000 in both savings and on hand there is not. Instead there is "+person.wallet.getOnHand()+" on hand and "
				+person.wallet.getInBank()+" in the bank making for a total balance of "+person.wallet.getBalance(), 
				person.wallet.getBalance() == 10000 
				&& person.wallet.getOnHand() == 5000 
				&& person.wallet.getInBank() == 5000);
		assertTrue("There should be no roles active in this person, there are", !person.active());
		assertTrue("The goToWork event should be on the top of the queue, it is not, instead the top event is "+person.toDo.peek().type, 
				person.toDo.peek().type == EventType.HostEvent);

		//Start up 
		person.msgNewHour(7); 
		assertTrue("The person's current time should be 7, it is not", person.getTime() == 7);
		assertTrue("The person should add a new event to the queue because he is created hungry and work is 2 hours away, he went back to sleep", 
				person.pickAndExecuteAnAction());
		assertTrue("There should be a restaurant event in the queue now, there is not. Instead there are "+person.toDo.size(), person.toDo.size() == 4);
		assertTrue("The person should return true and go to a restaurant of his choosing, he did not", person.pickAndExecuteAnAction());
		assertTrue("The person should have a customer role active, he does not", person.active());





	}

}
