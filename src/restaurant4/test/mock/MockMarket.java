package restaurant4.test.mock;


import restaurant.interfaces.Cashier;
import restaurant.interfaces.Market;

/**
 * A sample MockMarket built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Restaurant4Cashier cashier;
	public EventLog log;
	public MockMarket(String name) {
		super(name);
		log = new EventLog();
	}

	public void msgPayingBill(double bill) {
		log.add(new LoggedEvent("Received msgPayingBill from cashier. Bill = "+ bill));
	}
	
	public void msgCannotPay(String contract){
		log.add(new LoggedEvent("Received msgCannotPay from cashier. Contract says " + contract));
	}
	

}
