package resident.test;

import java.text.DecimalFormat;  
import java.util.ArrayList;
import java.util.List;

import resident.HomeOwnerAgent;
import resident.HomeOwnerAgent.MyFood;
import resident.test.mock.MockMaintenancePerson;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 */
public class HomeOwnerTest extends TestCase
{
	// These are instantiated for each test separately via the setUp() method.
	HomeOwnerAgent homeOwner;
	MockMaintenancePerson housekeeper;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		homeOwner = new HomeOwnerAgent("HomeOwner");		
		housekeeper = new MockMaintenancePerson("Mock Maintenance");	
	}
	
	/**
	 * This will test the resident eating dinner at home, with a nonempty fridge.
	 */
	public void testEatingDinnerAtHome() {
		System.out.println("Testing Eating at Home with Nonempty Fridge");
		
		// Adds food to the fridge 
		homeOwner.myFridge.add(new MyFood("Chicken", 1));
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: fridge has one food in it
		assertEquals("Home owner has one food item in it.", 1, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		// Send message to the home owner saying hungry
		homeOwner.msgGotHungry();
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has one logged event.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has one task
		assertEquals("Home owner now has one task - to eat.", 1, homeOwner.toDoList.size());
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has only one task now
		assertEquals("Home owner has one task - to cook.", 1, homeOwner.toDoList.size());
		
		// Checks that the home owner's log contains two entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has two logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has food. I can cook now!"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has two more logged events - one indicating what food it's going to cook, and the other saying no more of that item
		assertEquals("Home owner has four logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 0."));
		assertTrue(homeOwner.log.containsString("My fridge has no more Chicken."));
	}
}
