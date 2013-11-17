package resident.test;

import java.text.DecimalFormat;  
import java.util.ArrayList;
import java.util.List;

import resident.HomeOwnerAgent;
import resident.HomeOwnerAgent.MyFood;
import resident.HomeOwnerAgent.MyPriority;
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
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has only one task now
		assertEquals("Home owner has one task - to cook.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Cooking);
		
		// Checks that the home owner's log contains two entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has two logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has food. I can cook now!"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has two more logged events - one indicating what food it's going to cook, and the other saying no more of that item
		assertEquals("Home owner has four logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 0."));
		assertTrue(homeOwner.log.containsString("My fridge has no more Chicken."));
		
		// Ensures home owner has nothing in to do list
		assertEquals("Home owner has nothing to do while cooking.", 0, homeOwner.toDoList.size());
		
		// Messages the home owner saying that the food is ready to be eaten
		homeOwner.msgFoodDone();
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Eating);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Home owner has five logged events now.", 5, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My food is ready! I can eat now."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Home owner has nothing to do besides eating.", 0, homeOwner.toDoList.size());
		
		// Messages home owner when done eating
		homeOwner.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Home owner has six logged events now.", 6, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// Messages home owner when done washing dishes
		homeOwner.msgDoneWashing(homeOwner.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Home owner has seven logged events now.", 7, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());
	}
}
