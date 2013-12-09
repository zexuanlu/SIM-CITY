package restaurant2.interfaces;

import restaurant2.Check;
import restaurant2.interfaces.Restaurant2Waiter;
import utilities.restaurant.RestaurantCashier;

public interface Restaurant2Cashier extends RestaurantCashier{
	public abstract void msgComputeCheck(String orderString, Restaurant2Waiter waiter, Restaurant2Customer customer);
	public abstract void msgPayment(Restaurant2Customer c, Check check);
}
