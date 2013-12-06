package restaurant4.test.mock;


import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public class MockWaiter extends Mock implements Restaurant4Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Restaurant4Cashier cashier;
	public EventLog log;
	public MockWaiter(String name) {
		super(name);
		log = new EventLog();

	}

	@Override
	public void msgHereIsCheck(Restaurant4Customer c, double money) {
		log.add(new LoggedEvent("Received msgHereIsCheck from cashier. Customer = "+ c +"Change = "+ money));
	}

}
