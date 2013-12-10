package restaurant4.test.mock;


import restaurant4.interfaces.*;

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

	@Override
	public void msgReadyToOrder(Restaurant4Customer customer) {
		log.add(new LoggedEvent("Receieved msgReadyToOrder from Customer"));
	}

	@Override
	public void msgHereIsOrder(Restaurant4Customer customer, String choice) {
		log.add(new LoggedEvent("Received msgHereIsOrder from Customer"));		
	}

	@Override
	public void msgINeedCheck(Restaurant4Customer customer) {
		log.add(new LoggedEvent("Received msgINeedCheck from Customer"));
	}

	@Override
	public void msgLeavingTable(Restaurant4Customer customer) {
		log.add(new LoggedEvent("Received msgLeavingTable from Customer"));
	}

	@Override
	public void msgCannotGoOnBreak() {
		log.add(new LoggedEvent("Received msgCannotGoToBreak from Host"));
	}

	@Override
	public void msgCanGoOnBreak() {
		log.add(new LoggedEvent("Received msgCanGoOnBreak from Host"));
	}

	@Override
	public void msgSeatCustomer(Restaurant4Customer customer, int tableNumber, Restaurant4Host host, String string) {
		log.add(new LoggedEvent("Received msgSeatCustomer from Host"));
	}

	@Override
	public void msgWorkDayOver() {
		log.add(new LoggedEvent("Received msgWorkDayOver from Host"));
	}

}
