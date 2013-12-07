package restaurant1.interfaces;

import restaurant1.Restaurant1CustomerRole;
import utilities.restaurant.RestaurantHost;

public interface Restaurant1Host extends RestaurantHost {
	public abstract void msgIWantFood(Restaurant1CustomerRole cust, int loc);
	
	public abstract void msgLeavingTable(Restaurant1Waiter w, Restaurant1Customer c);
	
	public void msgDecidestatus(boolean a, Restaurant1CustomerRole c);
}
