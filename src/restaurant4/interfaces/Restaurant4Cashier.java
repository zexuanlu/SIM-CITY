package restaurant4.interfaces;

import utilities.restaurant.RestaurantCashier;
/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public interface Restaurant4Cashier extends RestaurantCashier{
	
	public abstract void msgBillChecked(double price);
	/**
	 * @param food The food that the customer ordered
	 * @param c The customer who needs to pay
	 * @param w The waiter who is serving the customer
	 *
	 * Sent by the waiter asking for a customer's check
	 */
	public abstract void msgINeedCheck(String food, Restaurant4Customer c, Restaurant4Waiter w);

	/**
	 * @param c The customer who is paying
	 * @param money the customer's money to pay for food
	 *
	 * Sent by the customer to pay for his food
	 */
	public abstract void msgPayingForFood(Restaurant4Customer c, double money);
}