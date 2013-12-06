package restaurant4.interfaces;

import restaurant4.Restaurant4CashierRole;
import restaurant4.Restaurant4Menu;
import restaurant4.Restaurant4WaiterRole;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Restaurant4Customer {
	
	public void msgSitAtTable(int tableNum, Restaurant4Waiter waiter, Restaurant4Menu menu);
	
	/**
	 * @param change The change from the cashier
	 *
	 * Sent by the cashier giving the customer his change after he receives the payment
	 */
	public abstract void msgHereIsChange(double change);

	public void msgReadyForOrder();

	public void msgHereIsFood(String choice);

	public void msgReOrder(Restaurant4Menu menu);

	public void msgHereIsCheck(double price, Restaurant4Cashier cashier);

	public void msgRestaurantFull();

}