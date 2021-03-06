package restaurant6.test;

import resident.test.mock.MockPerson;
import restaurant6.Restaurant6CookRole;
import restaurant6.Restaurant6Order;
import restaurant6.test.mock.MockSDWaiter;
import restaurant6.test.mock.MockWaiter;
import restaurant6.Restaurant6RevolvingStand;
import junit.framework.TestCase;

public class CookTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	Restaurant6CookRole cook;
	MockSDWaiter waiter;
	MockPerson person;
	Restaurant6RevolvingStand stand;
	MockWaiter regWaiter;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new MockPerson("Mock Person");
		cook = new Restaurant6CookRole("Cook", person);
		waiter = new MockSDWaiter("Mock Shared Data Waiter");
		regWaiter = new MockWaiter("Mock Regular Waiter");
		
		stand = new Restaurant6RevolvingStand();
	}
	
	/**
	 * Tests the cook's interaction with the revolving stand waiter
	 */
	public void testSharedData() {
		System.out.println("Testing the Cook Having Shared Data Orders");
		
		cook.setStand(stand);

		stand.insert(new Restaurant6Order("Green Tea Ice Cream", 1, waiter));
		
		// Preconditions: cook has nothing in his cook orders
		assertEquals("Cook should have nothing in his orders list.", 0, cook.cookOrders.size());
		
		// Cook should have nothing in his event log
		assertEquals("Cook should have nothing in his event log.", 0, cook.log.size());
		
		// Mock waiter should have nothing in his event log
		assertEquals("Mock Waiter should have nothing in event log.", 0, waiter.log.size());
		
		// Messages the cook that revolving stand needs to be checked
		cook.msgCheckStand();
		
		// Cook should have one entry in event log
		assertEquals("Cook should have one entry in his log.", 1, cook.log.size());
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("I have to go check the revolving stand for orders!"));
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two entries in event log - should check inventory because it's first in scheduler
		assertEquals("Cook should have one more entry in his log.", 2, cook.log.size());
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Checking my inventory.."));
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two more entries in event log - should have shared data
		assertEquals("Cook should have two more entries in his log.", 4, cook.log.size());
		assertTrue(cook.log.containsString("Checking stand.."));
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Picked up order of Green Tea Ice Cream"));
		
		// Cook should have one order that needs to be cooked
		assertEquals("Cook should have 1 order.", 1, cook.cookOrders.size());
		assertEquals(cook.cookOrders.get(0).getOrder(), "Green Tea Ice Cream");
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two more entries in event log - should have shared data
		assertEquals("Cook should have two more entries in his log.", 6, cook.log.size());
		assertTrue(cook.log.containsString("Checking my inventory.."));
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Cooking order.."));
		
		// Checks order status
		assertEquals("Order should have status of cooking.", Restaurant6Order.OrderState.Cooking, cook.cookOrders.get(0).getOrderStatus());
	
		// Changes order status to cooked
		cook.cookOrders.get(0).setOrderStatus(Restaurant6Order.OrderState.Cooked);
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Checks that the waiter's log now contains one entry
		assertEquals("Waiter has one logged entry.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Shared Data Waiter received message that order of Green Tea Ice Cream is ready."));
		
		// Cook's orders should now be 0
		assertEquals(cook.cookOrders.size(), 0);
		
		// Invokes scheduler, should return false
		assertFalse(cook.pickAndExecuteAnAction());
	}
	
	/**
	 * Tests cook if he has multiple orders on the revolving stand
	 */
	public void testSharedDataOrderAndRegularOrder() {
		System.out.println("Testing the Cook Having Shared Data Orders and Regular Orders");
		
		cook.setStand(stand);
		
		stand.insert(new Restaurant6Order("Green Tea Ice Cream", 1, waiter));
		
		// Preconditions: cook has nothing in his cook orders
		assertEquals("Cook should have nothing in his orders list.", 0, cook.cookOrders.size());
		
		// Cook should have nothing in his event log
		assertEquals("Cook should have nothing in his event log.", 0, cook.log.size());
		
		// Mock waiter should have nothing in his event log
		assertEquals("Mock Waiter should have nothing in event log.", 0, waiter.log.size());
		
		// Messages the cook that revolving stand needs to be checked
		cook.msgCheckStand();
		
		// Cook should have one entry in event log
		assertEquals("Cook should have one entry in his log.", 1, cook.log.size());
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("I have to go check the revolving stand for orders!"));
		
		// Messages cook an order from a regular waiter
		cook.hereIsAnOrder(new Restaurant6Order("Mint Chip Ice Cream", 2, regWaiter));
		
		// Cook should have two entries in event log
		assertEquals("Cook should have one more entry in his log.", 2, cook.log.size());
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Order Mint Chip Ice Cream received"));
		
		// Order should have status of pending
		assertEquals("Mint Chip Ice Cream should have pending status.", cook.cookOrders.get(0).getOrderStatus(), Restaurant6Order.OrderState.Pending);
				
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two entries in event log - should check inventory because it's first in scheduler
		assertEquals("Cook should have one more entry in his log.", 3, cook.log.size());
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Checking my inventory.."));
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two more entries in event log - should have shared data
		assertEquals("Cook should have 2 more entries in his log.", 5, cook.log.size());
		assertTrue(cook.log.containsString("Checking stand.."));
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Picked up order of Green Tea Ice Cream"));
		
		// Cook should have two orders that needs to be cooked
		assertEquals("Cook should have 2 orders.", 2, cook.cookOrders.size());
		assertEquals(cook.cookOrders.get(1).getOrder(), "Green Tea Ice Cream");
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two more entries in event log - should have shared data
		assertEquals("Cook should have two more entries in his log.", 7, cook.log.size());
		assertTrue(cook.log.containsString("Checking my inventory.."));
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Cooking order.."));
		
		// Checks order status
		assertEquals("First order should be Mint Chip Ice Cream.", "Mint Chip Ice Cream", cook.cookOrders.get(0).getOrder());
		assertEquals("Order should have status of cooking.", Restaurant6Order.OrderState.Cooking, cook.cookOrders.get(0).getOrderStatus());
	
		// Changes order status to cooked
		cook.cookOrders.get(0).setOrderStatus(Restaurant6Order.OrderState.Cooked);
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Cook should have two more entries in event log - should have shared data
		assertEquals("Cook should have two more entries in his log.", 9, cook.log.size());
		assertTrue(cook.log.containsString("Checking my inventory.."));
		assertTrue(cook.log.getLastLoggedEvent().toString().contains("Cooking order.."));
		
		// Checks order status
		assertEquals("First order should be Green Tea Ice Cream.", "Green Tea Ice Cream", cook.cookOrders.get(1).getOrder());
		assertEquals("Order should have status of cooking.", Restaurant6Order.OrderState.Cooking, cook.cookOrders.get(1).getOrderStatus());
	
		// Changes order status to cooked
		cook.cookOrders.get(0).setOrderStatus(Restaurant6Order.OrderState.Cooked);
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Checks that the waiter's log now contains one entry
		assertEquals("Regular Waiter has one logged entry.", 1, regWaiter.log.size());
		assertTrue(regWaiter.log.getLastLoggedEvent().toString().contains("Regular waiter received message that order of Mint Chip Ice Cream is ready."));
		
		// Cook's orders should now be 1
		assertEquals(cook.cookOrders.size(), 1);
		
		// Cook's first order should be Chicken
		assertEquals(cook.cookOrders.get(0).getOrder(), "Green Tea Ice Cream");
		
		// Invokes scheduler, should return false
		assertFalse(cook.pickAndExecuteAnAction());
		
		// Changes order status to cooked
		cook.cookOrders.get(0).setOrderStatus(Restaurant6Order.OrderState.Cooked);
		
		// Invokes the scheduler and makes sure that it returns true
		assertTrue(cook.pickAndExecuteAnAction());
		
		// Checks that the waiter's log now contains one entry
		assertEquals("Waiter has one logged entry.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Shared Data Waiter received message that order of Green Tea Ice Cream is ready."));
		
		// Cook's orders should now be 0
		assertEquals(cook.cookOrders.size(), 0);
		
		// Invokes scheduler, should return false
		assertFalse(cook.pickAndExecuteAnAction());		
	}
}
