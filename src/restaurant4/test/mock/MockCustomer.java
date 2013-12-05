package restaurant4.test.mock;


import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public class MockCustomer extends Mock implements Restaurant4Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Restaurant4Cashier cashier;
	public EventLog log;
	public MockCustomer(String name) {
		super(name);
		log = new EventLog();

	}

	@Override
	public void msgHereIsChange(double change) {
		log.add(new LoggedEvent("Received msgHereIsChange from cashier. Change = "+ change));
	}

}
