package restaurant1.interfaces;

import utilities.restaurant.RestaurantCashier;

public interface Restaurant1Cashier extends RestaurantCashier{

	public abstract void msgCheckthePrice(Restaurant1Waiter w, Restaurant1Customer c, String choice);
	
	public abstract void msgPayment(Restaurant1Customer c, double paying);
}
