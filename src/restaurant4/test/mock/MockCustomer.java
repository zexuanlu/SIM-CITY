package restaurant4.test.mock;


import restaurant4.Restaurant4Menu;
import restaurant4.interfaces.*;

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

	@Override
	public void gotHungry() {
		log.add(new LoggedEvent("Received msgGotHungry from Person"));	
	}

	@Override
	public void msgSitAtTable(int tableNum, Restaurant4Waiter waiter,Restaurant4Menu menu) {
		log.add(new LoggedEvent("Received msgSitAtTable from Restaurant4Host"));
	}

	@Override
	public void msgReadyForOrder() {
		
	}

	@Override
	public void msgHereIsFood(String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgReOrder(Restaurant4Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsCheck(double price, Restaurant4Cashier cashier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRestaurantFull() {
		// TODO Auto-generated method stub
		
	}

}
