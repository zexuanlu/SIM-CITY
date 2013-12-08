package restaurant2.interfaces;

import restaurant2.Check;
import restaurant2.interfaces.Waiter;
import utilities.restaurant.RestaurantCashier;

public interface Cashier extends RestaurantCashier{
	public abstract void msgComputeCheck(String orderString, Waiter waiter, Customer customer);
	public abstract void msgPayment(Customer c, Check check);
}
