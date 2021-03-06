package resident.test;

import java.text.DecimalFormat; 
import java.util.ArrayList;
import java.util.List;

import market.Food;
import resident.HomeOwnerRole;
import resident.HomeOwnerRole.MyPriority;
import resident.test.mock.MockPerson;
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
	HomeOwnerRole homeOwner;
	MockPerson person;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception {
		super.setUp();		
		
		person = new MockPerson("Mock Person");
		homeOwner = new HomeOwnerRole(person, "HomeOwner", 1);	
	}
	
	/**
	 * This will test the resident eating dinner at home, with a nonempty fridge.
	 */
	public void testEatingDinnerAtHome() {
		System.out.println("Testing Eating at Home with Nonempty Fridge");
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: fridge has 2 foods in it
		assertEquals("Home owner has 2 food items in it.", 2, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		// STEP 1: Send message to the home owner saying hungry
		homeOwner.updateVitals(3, 5);
		
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
		
		// Checks that the home owner has 1 more logged event - one indicating what food it's going to cook
		assertEquals("Home owner has 3 logged events now.", 3, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 1."));
		
		// Ensures home owner has nothing in to do list
		assertEquals("Home owner has nothing to do while cooking.", 0, homeOwner.toDoList.size());
		
		// STEP 2: Messages the home owner saying that the food is ready to be eaten
		homeOwner.msgFoodDone();
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Eating);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Home owner has four logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My food is ready! I can eat now."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Home owner has nothing to do besides eating.", 0, homeOwner.toDoList.size());
		
		// STEP 3: Messages home owner when done eating
		homeOwner.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Home owner has five logged events now.", 5, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// STEP 4: Messages home owner when done washing dishes
		homeOwner.msgDoneWashing(homeOwner.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Home owner has six logged events now.", 6, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());
	}
	
	/**
	 * Tests the home owner calling the housekeeper and getting house maintained
	 */
	public void testMaintainHouseNormative() {
	
		System.out.println("Testing Maintaining Home");
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: home owner has house number of 1 and name of HomeOwner
		assertEquals("Home owner has been constructed with the correct information.", 1, homeOwner.getHouseNumber());
		assertEquals("Home owner has been constructed with the correct name.", "HomeOwner", homeOwner.getName());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		/*
		 *  STEP 1: Messages the home owner to maintain the house. In the full scenario, the person agent will send 
		 *  this message to the home owner role once each day has begun. Thus, this assumes that the home owner
		 *  needs home maintenance every day.
		 */
		homeOwner.msgMaintainHome();
		
		// Checks home owner's log for one new entry
		assertEquals("Home owner now has one logged entry.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("It's been a day. I need to clean my house!"));
		
		// Checks to make sure that the home owner now has a task of calling the housekeeper
		assertEquals("Home owner now has one task in to do list.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has task of maintaining home.", MyPriority.Task.MaintainHome, homeOwner.toDoList.get(0).task);
	
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has no tasks to complete
		assertEquals("Home owner should have no tasks.", 0, homeOwner.toDoList.size());
		
		// Checks home owner's log for one new entry
		assertEquals("Home owner now has two logged entries.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done maintaining home!"));
		
		// Scheduler should return false
		assertFalse(homeOwner.pickAndExecuteAnAction());
	}
	
	/**
	 * Tests home owner not having enough food in the fridge, and decides to go to the market to buy food.
	 */
	public void testNoFoodGoToMarket() { 
		System.out.println("Testing Eating at Home with Empty Fridge; Decides to go to Market");
		
		// Sets the amount of time that the resident has - how is this actually going to work? :( Maybe just use money instead
		homeOwner.setMoney(60);	
		
		// Removes fridge items		
		homeOwner.myFridge.remove(0);
		homeOwner.myFridge.remove(0);
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: fridge has no food in it
		assertEquals("Home owner has 0 food items in it.", 0, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		// STEP 1: Send message to the home owner saying hungry
		homeOwner.updateVitals(4, 5);
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has one logged event.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has one task
		assertEquals("Home owner now has one task - to eat.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has only one task now
		assertEquals("Home owner has one task - to decide whether or not to go to the market.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.NoFood);
		
		// Checks that the home owner's log contains two entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has two logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has 1 more logged event - to go to the market
		assertEquals("Home owner has 3 logged events now.", 3, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to go to the market. I have enough money to go and come home."));
		
		// Ensures home owner has going to market and cooking in to do list
		assertEquals("Home owner has two tasks.", 2, homeOwner.toDoList.size());
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, homeOwner.toDoList.get(0).task);
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.Cooking, homeOwner.toDoList.get(1).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Check that mock person has received message
		assertEquals("Mock person has one entry.", 1, person.log.size());
		assertTrue(person.log.getLastLoggedEvent().toString().contains("You've finished your role and have returned to your PersonAgent"));
				
		// Initializes list of groceries
		List<Food> groceries = new ArrayList<Food>();
		groceries.add(new Food("Chicken", 1));
		
		// STEP 2: Message from the person agent when it's done going to the market
		homeOwner.msgDoneGoingToMarket(groceries);
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Cooking);
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.RestockFridge);
		
		// Checks home owner's log for one indicating ready to restock the fridge
		assertEquals("Home owner has four logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		// Verifies the amount of food that the fridge has and the type of food it has
		assertEquals("Fridge should have one food in it.", 1, homeOwner.myFridge.size());
		assertEquals(homeOwner.myFridge.get(0).choice, "Chicken");
		assertEquals(homeOwner.myFridge.get(0).amount, 1);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has cooking task
		assertEquals("Home owner has nothing to do besides cook food.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has cooking food task.", MyPriority.Task.Cooking, homeOwner.toDoList.get(0).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has two more logged events - one indicating what food it's going to cook, and the other saying no more of that item
		assertEquals("Home owner has six logged events now.", 6, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 0."));
		assertTrue(homeOwner.log.containsString("My fridge has no more Chicken."));
		
		// Ensures home owner has nothing in to do list
		assertEquals("Home owner has nothing to do while cooking.", 0, homeOwner.toDoList.size());
		
		// STEP 2: Messages the home owner saying that the food is ready to be eaten
		homeOwner.msgFoodDone();
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Eating);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Home owner has seven logged events now.", 7, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My food is ready! I can eat now."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Home owner has nothing to do besides eating.", 0, homeOwner.toDoList.size());
		
		// STEP 3: Messages home owner when done eating
		homeOwner.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Home owner has eight logged events now.", 8, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// STEP 4: Messages home owner when done washing dishes
		homeOwner.msgDoneWashing(homeOwner.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Home owner has nine logged events now.", 9, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());		
	}
	
	/**
	 * Tests the home owner not having enough food in the fridge, and not enough time to cook, so goes to restaurant
	 */
	public void testNoFoodGoToRestaurant() {
		System.out.println("Testing Eating at Home with Empty Fridge; Decides to go to Restaurant");
		
		// Removes fridge items
		homeOwner.myFridge.remove(0);
		homeOwner.myFridge.remove(0);
		
		person.balance = 80;
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: fridge has one food in it
		assertEquals("Home owner has one food item in it.", 0, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		// STEP 1: Send message to the home owner saying hungry
		homeOwner.updateVitals(3, 5);
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has one logged event.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has one task
		assertEquals("Home owner now has one task - to eat.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has only one task now
		assertEquals("Home owner has one task - to decide whether or not to go to the market.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.NoFood);
		
		// Checks that the home owner's log contains two entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has two logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Ensures home owner has going to Restaurant and Market in to do list
		assertEquals("Home owner has 2 tasks.", 2, homeOwner.toDoList.size());
		assertEquals("Home owner has task of going to the restaurant.", MyPriority.Task.GoToRestaurant, homeOwner.toDoList.get(0).task);
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, homeOwner.toDoList.get(1).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Check that mock person has received message
		assertEquals("Mock person has one entry.", 1, person.log.size());
		assertTrue(person.log.getLastLoggedEvent().toString().contains("You've finished your role and have returned to your PersonAgent"));
		
		// STEP 2: Message from the person agent when it's done going to the restaurant
		homeOwner.msgDoneEatingOut();
		
		// Checks that the home owner has 1 more logged event of finishing eating out
		assertEquals("Home owner has 4 logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I just finished eating out. I'm full now!"));
		
		// Ensures home owner has going to Market left in to do list
		assertEquals("Home owner has 1 tasks.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, homeOwner.toDoList.get(0).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Ensures home owner has nothing left in to do list
		assertEquals("Home owner has no tasks.", 0, homeOwner.toDoList.size());
		
		// Makes sure that scheduler returns false, because home owner has nothing left to do
		assertFalse(homeOwner.pickAndExecuteAnAction());
		
		// Initializes list of groceries
		List<Food> groceries = new ArrayList<Food>();
		groceries.add(new Food("Chicken", 1));
		
		// STEP 3: Message from the person agent when it's done going to the market
		homeOwner.msgDoneGoingToMarket(groceries);
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.RestockFridge);
		
		// Checks home owner's log for one indicating ready to restock the fridge
		assertEquals("Home owner has 5 logged events now.", 5, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());		
		
		// Checks that to do list now is empty
		assertEquals("Home owner has 0 tasks now.", 0, homeOwner.toDoList.size());
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());
	}
	
	/**
	 * Test calling housekeeper and eating at home with nonempty fridge
	 */
	public void testMaintainHomeAndEatAtHome() {
		System.out.println("Testing Maintaining Home & Eating Dinner at Home");
		
		// Sets the home owner's money to $60
		homeOwner.setMoney(60);
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: home owner has house number of 1 and name of HomeOwner
		assertEquals("Home owner has been constructed with the correct information.", 1, homeOwner.getHouseNumber());
		assertEquals("Home owner has been constructed with the correct name.", "HomeOwner", homeOwner.getName());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		/*
		 *  STEP 1: Messages the home owner to maintain the house. In the full scenario, the person agent will send 
		 *  this message to the home owner role once each day has begun. Thus, this assumes that the home owner
		 *  needs home maintenance every day.
		 */
		homeOwner.msgMaintainHome();
		
		// Checks home owner's log for one new entry
		assertEquals("Home owner now has one logged entry.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("It's been a day. I need to clean my house!"));
		
		// Checks to make sure that the home owner now has a task of maintaining the home
		assertEquals("Home owner now has one task in to do list.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has task of calling the housekeeper.", MyPriority.Task.MaintainHome, homeOwner.toDoList.get(0).task);
	
		// Adds food to the fridge 
		homeOwner.myFridge.add(new Food("Chicken", 1));
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has 1 logged event.", 1, homeOwner.log.size());
		
		// Preconditions: fridge has 3 food in it
		assertEquals("Home owner has 3 food items in it.", 3, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has 1 task in to do list.", 1, homeOwner.toDoList.size());
		
		// STEP 2: Send message to the home owner saying hungry
		homeOwner.updateVitals(3, 5);
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has 2 logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has two tasks
		assertEquals("Home owner now has a new task - to eat.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.NeedToEat);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Scheduler should choose the check fridge action, since eating is a higher priority
		// Checks that the home owner has only task to cook
		assertEquals("Home owner has the task of cooking.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.Cooking);
		
		// Checks that the home owner's log contains 3 entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has 3 logged events.", 3, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has food. I can cook now!"));
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
	
		// Checks that home owner has 1 task to complete
		assertEquals("Home owner should have 1 task.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has task of cooking left.", MyPriority.Task.Cooking, homeOwner.toDoList.get(0).task);
	
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has two more logged events - one indicating what food it's going to cook, and the other saying no more of that item
		assertEquals("Home owner has 5 logged events now.", 5, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 1."));
		
		// Ensures home owner has nothing in to do list
		assertEquals("Home owner has nothing to do while cooking.", 0, homeOwner.toDoList.size());
		
		// STEP 4: Messages the home owner saying that the food is ready to be eaten
		homeOwner.msgFoodDone();
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Eating);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Home owner has 6 logged events now.", 6, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My food is ready! I can eat now.")); 
		
		// Checks that home owner has 1 tasks of eating the cooked food still
		assertEquals("Home owner should have 1 task of eating left.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner should have task of eating.", MyPriority.Task.Eating, homeOwner.toDoList.get(0).task);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Home owner has nothing to do besides eating.", 0, homeOwner.toDoList.size());
		
		// STEP 6: Messages home owner when done eating
		homeOwner.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Home owner has 7 logged events now.", 7, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// STEP 7: Messages home owner when done washing dishes
		homeOwner.msgDoneWashing(homeOwner.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Home owner has 8 logged events now.", 8, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());		
	}
	
	public void testGoToMarketAndMaintainHome() {
	
		System.out.println("Testing Maintaining Home & Going to Market to Get Dinner");
	
		homeOwner.setMoney(50);
		
		// Removes fridge items
		homeOwner.myFridge.remove(0);
		homeOwner.myFridge.remove(0);
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has no logged events. It doesn't.", 0, homeOwner.log.size());
		
		// Preconditions: home owner has house number of 1 and name of HomeOwner
		assertEquals("Home owner has been constructed with the correct information.", 1, homeOwner.getHouseNumber());
		assertEquals("Home owner has been constructed with the correct name.", "HomeOwner", homeOwner.getName());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has no tasks in to do list. It doesn't.", 0, homeOwner.toDoList.size());
		
		/*
		 *  STEP 1: Messages the home owner to maintain the house. In the full scenario, the person agent will send 
		 *  this message to the home owner role once each day has begun. Thus, this assumes that the home owner
		 *  needs home maintenance every day.
		 */
		homeOwner.msgMaintainHome();
		
		// Checks home owner's log for one new entry
		assertEquals("Home owner now has one logged entry.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("It's been a day. I need to clean my house!"));
		
		// Checks to make sure that the home owner now has a task of maintaining the home
		assertEquals("Home owner now has one task in to do list.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner has task of maintaining home.", MyPriority.Task.MaintainHome, homeOwner.toDoList.get(0).task);
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Home owner has 1 logged event.", 1, homeOwner.log.size());
		
		// Preconditions: fridge has no food in it
		assertEquals("Home owner has no food items in it.", 0, homeOwner.myFridge.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Home owner has 1 task in to do list.", 1, homeOwner.toDoList.size());
		
		// STEP 2: Send message to the home owner saying hungry
		homeOwner.updateVitals(3, 5);
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has 2 logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has two tasks
		assertEquals("Home owner now has a new task - to eat.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.NeedToEat);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has task of deciding whether or not to go to the market
		assertEquals("Home owner has a task of deciding whether or not to go to the market.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.NoFood);
		
		// Checks that the home owner's log contains 3 entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has three logged events.", 3, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
	
		// Invokes the home owner's scheduler and makes sure that it returns true. Should decide to go to market because of scheduler priority
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has 1 more logged event - to go to the market
		assertEquals("Home owner has 4 logged events now.", 4, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to go to the market. I have enough money to go and come home."));
		
		// Ensures home owner has going to market and cooking in to do list
		assertEquals("Home owner has three tasks.", 3, homeOwner.toDoList.size());
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, homeOwner.toDoList.get(1).task);
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.Cooking, homeOwner.toDoList.get(2).task);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Home owner should have gone to the market, so only cooking and calling housekeeper are in to-do list. Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.MaintainHome);
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.Cooking);
	
		// Initializes list of groceries
		List<Food> groceries = new ArrayList<Food>();
		groceries.add(new Food("Chicken", 1));
		
		// STEP 3: Message from the person agent when it's done going to the market
		homeOwner.msgDoneGoingToMarket(groceries);
		
		// Checks that to do list now has the task to restock fridge, cook, and call housekeeper.
		assertEquals("Home owner has three tasks now.", 3, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.MaintainHome);
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.Cooking);
		assertEquals(homeOwner.toDoList.get(2).task, MyPriority.Task.RestockFridge);
		
		// Checks home owner's log for one indicating ready to restock the fridge
		assertEquals("Home owner has 5 logged events now.", 5, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		// Verifies the amount of food that the fridge has and the type of food it has
		assertEquals("Fridge should have one food in it.", 1, homeOwner.myFridge.size());
		assertEquals(homeOwner.myFridge.get(0).choice, "Chicken");
		assertEquals(homeOwner.myFridge.get(0).amount, 1);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
	
		// Checks that the home owner has 2 tasks to complete - cooking and restocking the fridge
		assertEquals("Home owner should have two tasks left.", 2, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.Cooking);
		assertEquals(homeOwner.toDoList.get(1).task, MyPriority.Task.RestockFridge);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks to make sure resident has cooking task
		assertEquals("Home owner has nothing to do besides cook food.", 1, homeOwner.toDoList.size());
		assertEquals(MyPriority.Task.Cooking, homeOwner.toDoList.get(0).task);
		
		// Checks that home owner has one more logged event noting maintained home
		assertEquals("Home owner has 6 logged events.", 6, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("Done maintaining home!"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that the home owner has two more logged events - one indicating what food it's going to cook, and the other saying no more of that item
		assertEquals("Home owner has 8 logged events now.", 8, homeOwner.log.size());
		assertTrue(homeOwner.log.containsString("I'm going to cook Chicken. My inventory of it is now 0."));
		assertTrue(homeOwner.log.containsString("My fridge has no more Chicken."));
		
		// Ensures home owner has nothing in to do list
		assertEquals("Home owner has nothing to do while cooking.", 0, homeOwner.toDoList.size());
		
		// STEP 4: Messages the home owner saying that the food is ready to be eaten
		homeOwner.msgFoodDone();
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Home owner has 9 logged events now.", 9, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My food is ready! I can eat now.")); 
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has no tasks to complete
		assertEquals("Home owner should have no tasks left.", 0, homeOwner.toDoList.size());
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertFalse(homeOwner.pickAndExecuteAnAction());
		
		// STEP 5: Messages home owner when done eating
		homeOwner.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Home owner has 10 logged events now.", 10, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		assertEquals(homeOwner.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes the home owner's scheduler and makes sure that it returns true
		assertTrue(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has 1 tasks of eating the cooked food still
		assertEquals("Home owner should have 1 task of eating left.", 1, homeOwner.toDoList.size());
		assertEquals("Home owner should have task of eating.", MyPriority.Task.Washing, homeOwner.toDoList.get(0).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertFalse(homeOwner.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Home owner has one task now.", 1, homeOwner.toDoList.size());
		
		// STEP 7: Messages home owner when done washing dishes
		homeOwner.msgDoneWashing(homeOwner.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Home owner has 11 logged events now.", 11, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(homeOwner.pickAndExecuteAnAction());				
	}
}
