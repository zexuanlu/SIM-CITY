package restaurant6.test.mock;

import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Order;
import restaurant6.gui.Restaurant6WaiterGui;
import restaurant6.interfaces.Restaurant6Customer;
import restaurant6.interfaces.Restaurant6Waiter;

/**
 * Mock Waiter class to test waiter/cashier interaction
 * @author jenniezhou
 *
 */
public class MockWaiter extends Mock implements Restaurant6Waiter {
	
	public EventLog log = new EventLog();

	public MockWaiter(String name) {
		super(name);
	}

	/**
	 * 	Waiter receives check from cashier
	 */
	public void msgHereIsTheCheck(Restaurant6Check c) {
		log.add(new LoggedEvent("Received check from cashier."));
	}

	/**
	 * Customer is ready to order
	 */
	public void readyToOrder(Restaurant6Customer customerAgent) {
	}

	/**
	 * Customer has finished eating and is leaving the restaurant
	 */
	public void doneEatingAndLeaving(Restaurant6Customer customerAgent) {
	}

	/**
	 * Customer can't afford anything, so is leaving the restaurant
	 */
	public void cannotAffordAnything(Restaurant6Customer customerAgent) {
	}

	/**
	 * Customer gives waiter the order
	 */
	public void hereIsMyOrder(String myChoice, Restaurant6Customer customerAgent) {
	}

	public void seatAtTable(Restaurant6Customer customer, int tableNum) {
	}

	public void msgCustomerAtFront() {
	}

	public void outOfFood(Restaurant6Order o) {
	}

	public void orderIsReady(Restaurant6Order o) {
	}

	public void goOnBreak(boolean allowedToGoOnBreak) {
	}


}
