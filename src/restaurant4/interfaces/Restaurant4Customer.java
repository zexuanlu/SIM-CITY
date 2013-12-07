package restaurant4.interfaces;

import restaurant4.Restaurant4Menu;
import utilities.restaurant.RestaurantCustomer;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Restaurant4Customer extends RestaurantCustomer{
	
	public abstract void msgSitAtTable(int tableNum, Restaurant4Waiter waiter, Restaurant4Menu menu);

	public abstract void msgHereIsChange(double change);

	public abstract void msgReadyForOrder();

	public abstract void msgHereIsFood(String choice);

	public abstract void msgReOrder(Restaurant4Menu menu);

	public abstract void msgHereIsCheck(double price, Restaurant4Cashier cashier);

	public abstract void msgRestaurantFull();
}