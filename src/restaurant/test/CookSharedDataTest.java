package restaurant.test;

import restaurant.Restaurant1CookRole;
import restaurant.test.mock.MockSDWaiter;
import restaurant.shareddata.*;

import junit.framework.TestCase;

public class CookSharedDataTest extends TestCase{
	private final int table = 1;
	
	MockSDWaiter waiter;
	Restaurant1CookRole cook;
	Restaurant1RevolvingStand rStand;
	Order order;
	
	protected void setUp(){
		// initializing agents
		waiter = new MockSDWaiter();
		rStand = new Restaurant1RevolvingStand();
		cook = new Restaurant1CookRole("TestCook");
		cook.setRevStand(rStand);
		order = new Order(waiter, "Steak", table);
	}
	
	protected void tearDown(){
		
	}
	
	private void runScheduler(){
		cook.pickAndExecuteAnAction();
	}
	
	public void testOneRevolvingOrder() {
		// assert initializations
		assertEquals("Cook's orders should be empty", 0, cook.order.size());
		assertEquals("Revolving stand should be empty", true, rStand.isEmpty());
		assertEquals("sendTruckBack should be false", false, cook.sendTruckBack);
		
		// add order to stand
		rStand.insertOrder(order);
		
		// let cook check for order
		runScheduler();
		
		// See if cook picked up order
		assertEquals("Cook should have one order", 1, cook.order.size());
	}

}
