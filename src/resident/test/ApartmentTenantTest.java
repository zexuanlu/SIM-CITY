package resident.test;

import java.util.ArrayList;
import java.util.List;

import market.Food;
import resident.ApartmentTenantRole; 
import resident.ApartmentTenantRole.MyPriority;
import resident.test.mock.MockApartmentLandlord;
import resident.test.mock.MockPerson;
import junit.framework.TestCase;

public class ApartmentTenantTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	MockPerson person;
	ApartmentTenantRole tenant;
	MockApartmentLandlord landlord;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		person = new MockPerson("Mock Person");
		tenant = new ApartmentTenantRole("Tenant", 1, person);		
		landlord = new MockApartmentLandlord("Mock Apartment Landlord");	
	}
	
	/**
	 * Tests the apartment tenant paying rent in the normative scenario (isolated, no other tasks)
	 */
	public void testPayingRentNormative() {
		System.out.println("Testing the tenant paying the landlord with no other tasks to complete");
		
		tenant.setLandlord(landlord);
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has one logged entry
		assertEquals("Tenant has 1 logged entry now.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 1 task now.", 1, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has no tasks
		assertEquals("Tenant has 0 tasks now.", 0, tenant.toDoList.size());
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(0);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 2 logged entries now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $0"));
		
		// Tenant still has no tasks to do
		assertEquals("Tenant has no tasks.", 0, tenant.toDoList.size());
		
		// Scheduler should return false
		assertFalse(tenant.pickAndExecuteAnAction());
	}
	/**
	 * Tests the apartment tenant not having enough money to pay rent
	 */
	public void testNotEnoughToPayRent() {
		System.out.println("Testing the tenant paying the landlord with not enough money");
		
		tenant.setMoney(10);
		
		tenant.setLandlord(landlord);
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has one logged entry
		assertEquals("Tenant has 1 logged entry now.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 1 task now.", 1, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has no tasks
		assertEquals("Tenant has 0 tasks now.", 0, tenant.toDoList.size());
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(10);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 2 logged entries now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $10"));
		
		// Tenant still has no tasks to do
		assertEquals("Tenant has no tasks.", 0, tenant.toDoList.size());
		
		// Makes sure that the scheduler returns false, since the tenant has nothing to do
		assertFalse(tenant.pickAndExecuteAnAction());		
	}
	
	/**
	 * Tests when the tenant has to pay rent and eats with a fully stocked fridge
	 */
	public void testPayRentAndEatAtHome() {
		System.out.println("Testing Eating at Home with Nonempty Fridge and Paying Rent");
		
		// Adds food to the fridge 
		tenant.myFridge.add(new Food("Chicken", 1));
		
		// Preconditions: before tenant gets hungry message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: fridge has 3 food in it
		assertEquals("Tenant has 3 food items in it.", 3, tenant.myFridge.size());
		
		// Preconditions: check to make sure tenant has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// STEP 1: Send message to the tenant saying hungry
		tenant.updateVitals(3, 5);
		
		// Check to make sure that the tenant's log contains one entry now, and is the hungry message
		assertEquals("Tenant now has one logged event.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the tenant now has one task
		assertEquals("Tenant now has one task - to eat.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		tenant.setLandlord(landlord);
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has 2 logged entries
		assertEquals("Tenant has 2 logged entry now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 2 tasks now.", 2, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(1).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has 1 task
		assertEquals("Tenant has 1 tasks now.", 1, tenant.toDoList.size());
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(0);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 3 logged entries now.", 3, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $0"));
		
		// Tenant still has 1 tasks to do
		assertEquals("Tenant has 1 tasks.", 1, tenant.toDoList.size());
		
		// Scheduler should return false
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that to do list now has the task to cook
		assertEquals("Tenant has 1 tasks now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.Cooking);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Tenant has four logged events now.", 4, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("My fridge has food. I can cook now!"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Tenant has nothing to do besides eating.", 0, tenant.toDoList.size());
		
		// STEP 3: Messages home owner when done eating
		tenant.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Tenant has six logged events now.", 6, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Tenant has one task now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Tenant has one task now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// STEP 4: Messages home owner when done washing dishes
		tenant.msgDoneWashing(tenant.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Tenant has seven logged events now.", 7, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return false because no task.
		assertTrue(tenant.pickAndExecuteAnAction());
	}
	
	/**
	 * Tests when the tenant has to pay rent and doesn't have enough, and eats at home.
	 */
	public void testNotEnoughRentAndEatAtHome() {
		System.out.println("Testing Eating at Home with Nonempty Fridge and Paying Rent");
		
		// Adds food to the fridge 
		tenant.myFridge.add(new Food("Chicken", 1));
		
		// Preconditions: before tenant gets hungry message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: fridge has 3 food in it
		assertEquals("Tenant has 3 food items in it.", 3, tenant.myFridge.size());
		
		// Preconditions: check to make sure tenant has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// STEP 1: Send message to the tenant saying hungry
		tenant.updateVitals(3, 5);
		
		// Check to make sure that the tenant's log contains one entry now, and is the hungry message
		assertEquals("Tenant now has one logged event.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the tenant now has one task
		assertEquals("Tenant now has one task - to eat.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		tenant.setLandlord(landlord);
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has 2 logged entries
		assertEquals("Tenant has 2 logged entry now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 2 tasks now.", 2, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(1).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has 1 task
		assertEquals("Tenant has 1 tasks now.", 1, tenant.toDoList.size());
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(10);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 3 logged entries now.", 3, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $10"));
		
		// Tenant still has 1 tasks to do
		assertEquals("Tenant has 1 tasks.", 1, tenant.toDoList.size());
		
		// Scheduler should return false
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that to do list now has the task to cook
		assertEquals("Tenant has 1 tasks now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.Cooking);
		
		// Checks home owner's log for one indicating ready to eat
		assertEquals("Tenant has four logged events now.", 4, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("My fridge has food. I can cook now!"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks to make sure resident has no tasks when eating
		assertEquals("Tenant has nothing to do besides eating.", 0, tenant.toDoList.size());
		
		// STEP 3: Messages home owner when done eating
		tenant.msgDoneEating();
		
		// Checks home owner's log for one indicating ready to wash dishes
		assertEquals("Tenant has six logged events now.", 6, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Done eating. I'm going to wash dishes now."));
		
		// Checks that to do list now has the task to wash dishes
		assertEquals("Tenant has one task now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.WashDishes);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that home owner has task of washing (to prevent scheduler from running same action multiple times)
		assertEquals("Tenant has one task now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.Washing);
		
		// STEP 4: Messages home owner when done washing dishes
		tenant.msgDoneWashing(tenant.toDoList.get(0));
		
		// Checks home owner's log for one indicating done washing dishes
		assertEquals("Tenant has seven logged events now.", 7, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Done washing dishes!"));
		
		// Invokes scheduler. Should return true
		assertTrue(tenant.pickAndExecuteAnAction());
	}
	
	/**
	 * This tests the tenant paying rent and then going to eat at a restaurant
	 */
	public void testPayRentEatRestaurant() {
		System.out.println("Testing the tenant paying the landlord and eating at a restaurant");
		
		tenant.setLandlord(landlord);
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has one logged entry
		assertEquals("Tenant has 1 logged entry now.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Removes fridge items
		tenant.myFridge.remove(0);
		tenant.myFridge.remove(0);
		
		person.balance = 80;
		
		// STEP 1: Send message to the home owner saying hungry
		tenant.updateVitals(3, 5);
		
		// Check to make sure that the home owner's log contains one entry now, and is the hungry message
		assertEquals("Home owner now has one logged event.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I'm hungry."));
		
		// Check to make sure that the home owner now has one task
		assertEquals("Home owner now has 2 tasks - to eat.", 2, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.PayRent);
		assertEquals(tenant.toDoList.get(1).task, MyPriority.Task.NeedToEat);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that the home owner has only one task now
		assertEquals("Home owner has one task - to decide whether or not to go to the market.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.NeedToEat);
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(0);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 3 logged entries now.", 3, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $0"));
		
		// Tenant still has no tasks to do
		assertEquals("Tenant has 1 task.", 1, tenant.toDoList.size());
		
		// Scheduler should return false
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Checks that the home owner's log contains two entries now. The most recent one is the full fridge entry.
		assertEquals("Home owner has 4 logged events.", 4, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("My fridge has no food. I must now decide if I should go to the market or go out to eat."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Ensures home owner has going to Restaurant and Market in to do list
		assertEquals("Home owner has 2 tasks.", 2, tenant.toDoList.size());
		assertEquals("Home owner has task of going to the restaurant.", MyPriority.Task.GoToRestaurant, tenant.toDoList.get(0).task);
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, tenant.toDoList.get(1).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that mock person has received message
		assertEquals("Mock person has one entry.", 1, person.log.size());
		assertTrue(person.log.getLastLoggedEvent().toString().contains("You've finished your role and have returned to your PersonAgent"));
		
		// STEP 2: Message from the person agent when it's done going to the restaurant
		tenant.msgDoneEatingOut();
		
		// Checks that the home owner has 1 more logged event of finishing eating out
		assertEquals("Home owner has 6 logged events now.", 6, tenant.log.size());
		assertTrue(tenant.log.containsString("I just finished eating out. I'm full now!"));
		
		// Ensures home owner has going to Market left in to do list
		assertEquals("Home owner has 1 tasks.", 1, tenant.toDoList.size());
		assertEquals("Home owner has task of going to the market.", MyPriority.Task.GoToMarket, tenant.toDoList.get(0).task);
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Ensures home owner has nothing left in to do list
		assertEquals("Home owner has no tasks.", 0, tenant.toDoList.size());
		
		// Makes sure that scheduler returns false, because home owner has nothing left to do
		assertFalse(tenant.pickAndExecuteAnAction());
		
		// Initializes list of groceries
		List<Food> groceries = new ArrayList<Food>();
		groceries.add(new Food("Chicken", 1));
		
		// STEP 3: Message from the person agent when it's done going to the market
		tenant.msgDoneGoingToMarket(groceries);
		
		// Checks that to do list now has the task to eat
		assertEquals("Home owner has one task now.", 1, tenant.toDoList.size());
		assertEquals(tenant.toDoList.get(0).task, MyPriority.Task.RestockFridge);
		
		// Checks home owner's log for one indicating ready to restock the fridge
		assertEquals("Home owner has 7 logged events now.", 7, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I just finished going to the market. Time to put all my groceries in the fridge."));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());		
		
		// Checks that to do list now is empty
		assertEquals("Home owner has 0 tasks now.", 0, tenant.toDoList.size());
		
		// Invokes scheduler. Should return false because no task.
		assertFalse(tenant.pickAndExecuteAnAction());
	}
}
